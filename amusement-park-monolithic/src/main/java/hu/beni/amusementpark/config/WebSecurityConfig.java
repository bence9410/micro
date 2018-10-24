package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.COULD_NOT_FIND_USER;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.ERROR;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.UNEXPECTED_ERROR_OCCURED;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ConditionalOnWebApplication
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final ObjectMapper objectMapper;

	public WebSecurityConfig(@Qualifier("_halObjectMapper") ObjectMapper objectMapper) {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		this.objectMapper = objectMapper;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public BeniAuthenticationSuccessHandler authenticationSuccessHandler(VisitorService visitorService,
			VisitorMapper visitorMapper) {
		return new BeniAuthenticationSuccessHandler(visitorService, objectMapper, visitorMapper);
	}

	@Bean
	public UserDetailsService userDetailsService(VisitorRepository visitorRepository) {
		return new UserDetailsServiceImpl(visitorRepository);
	}

	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		authenticationProvider.setUserDetailsService(userDetailsService(null));
		authenticationProvider.setHideUserNotFoundExceptions(false);
		return authenticationProvider;
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
		JdbcTokenRepositoryImpl jdbcTokenRepositoryImpl = new JdbcTokenRepositoryImpl() {
			protected void initDao() {
				try {
					getJdbcTemplate().execute(CREATE_TABLE_SQL);
				} catch (BadSqlGrammarException e) {
				}
			}
		};
		jdbcTokenRepositoryImpl.setDataSource(dataSource);
		return jdbcTokenRepositoryImpl;
	}

	@Bean
	public AbstractRememberMeServices rememberMeServices() {
		AbstractRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices("beni",
				userDetailsService(null), persistentTokenRepository(null));
		return rememberMeServices;
	}

	public UsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
		ValidateingUsernamePasswordAuthenticationFilter authenticationFilter = new ValidateingUsernamePasswordAuthenticationFilter();
		authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler(null, null));
		authenticationFilter.setAuthenticationFailureHandler(new BeniAuthenticationFailureHandler());
		authenticationFilter.setAuthenticationManager(authenticationManagerBean());
		authenticationFilter.setRememberMeServices(rememberMeServices());
		return authenticationFilter;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider())
				.authenticationProvider(new RememberMeAuthenticationProvider("beni"));
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http //@formatter:off
            .authorizeRequests()
            	.antMatchers("/", "/webjars/**", "/index.js", "/links", "/me", "/signUp")
            	.permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new RememberMeAuthenticationFilter(
    				authenticationManagerBean(), rememberMeServices()), UsernamePasswordAuthenticationFilter.class)
            .logout()
            	.addLogoutHandler(rememberMeServices())
            	.logoutSuccessUrl("/")
                .and()
            .csrf()
            	.disable()
            .exceptionHandling()
            	.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")); //@formatter:on
	}

	@RequiredArgsConstructor
	static class BeniAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

		private final VisitorService visitorService;
		private final ObjectMapper objectMapper;
		private final VisitorMapper visitorMapper;

		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
			response.setHeader(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE);
			response.getWriter().println(objectMapper.writeValueAsString(
					visitorMapper.toResource(visitorService.findByEmail(authentication.getName()))));
		}

	}

	@Slf4j
	static class BeniAuthenticationFailureHandler implements AuthenticationFailureHandler {

		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException exception) throws IOException, ServletException {
			log.error(ERROR, exception);
			response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
			if (UsernameNotFoundException.class.isInstance(exception)
					|| BadCredentialsException.class.isInstance(exception)) {
				response.getWriter().println(exception.getMessage());
			} else {
				response.getWriter().println(UNEXPECTED_ERROR_OCCURED);
			}
		}

	}

	@RequiredArgsConstructor
	static class UserDetailsServiceImpl implements UserDetailsService {

		private final VisitorRepository visitorRepository;

		@Override
		public UserDetails loadUserByUsername(String email) {
			Visitor visitor = visitorRepository.findByEmail(email)
					.orElseThrow(() -> new UsernameNotFoundException(String.format(COULD_NOT_FIND_USER, email)));
			return new User(email, visitor.getPassword(),
					Arrays.asList(new SimpleGrantedAuthority(visitor.getAuthority())));
		}

	}

	static class ValidateingUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

		private boolean postOnly = true;
		private int min = 5;
		private int max = 25;

		public ValidateingUsernamePasswordAuthenticationFilter() {
			setUsernameParameter("email");
		}

		@Override
		public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
			if (postOnly && !request.getMethod().equals("POST")) {
				throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
			}

			String username = validateEmail(obtainUsername(request));
			String password = validatePassword(obtainPassword(request));

			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
					password);

			setDetails(request, authRequest);

			return this.getAuthenticationManager().authenticate(authRequest);
		}

		private String validateEmail(String email) {
			return Optional.ofNullable(email).filter(this::isValidEmail)
					.orElseThrow(() -> new BadCredentialsException("Email must be a well-formed email address"));
		}

		private boolean isValidEmail(String email) {
			return email.matches(".+@.+\\..+");
		}

		private String validatePassword(String credential) {
			return Optional.ofNullable(credential).map(String::trim).filter(this::isLengthBetweenMinAndMax)
					.orElseThrow(() -> new BadCredentialsException(
							String.format("Password size must be between %d and %d", min, max)));
		}

		private boolean isLengthBetweenMinAndMax(String string) {
			return string.length() >= min && string.length() <= max;
		}

	}
}