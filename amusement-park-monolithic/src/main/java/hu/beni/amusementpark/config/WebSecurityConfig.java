package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.COULD_NOT_FIND_USER;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.UNEXPECTED_ERROR_OCCURED;
import static hu.beni.amusementpark.constants.RequestMappingConstants.INDEX_JS;
import static hu.beni.amusementpark.constants.RequestMappingConstants.LINKS;
import static hu.beni.amusementpark.constants.RequestMappingConstants.ME;
import static hu.beni.amusementpark.constants.RequestMappingConstants.SIGN_UP;
import static hu.beni.amusementpark.constants.RequestMappingConstants.SLASH;
import static hu.beni.amusementpark.constants.RequestMappingConstants.WEBJARS;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.EMAIL_MESSAGE;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.sizeMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;

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

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		authenticationProvider.setUserDetailsService(userDetailsService(null));
		authenticationProvider.setHideUserNotFoundExceptions(false);
		return authenticationProvider;
	}

	@Bean
	public UsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
		ValidateingUsernamePasswordAuthenticationFilter authenticationFilter = new ValidateingUsernamePasswordAuthenticationFilter();
		authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler(null, null));
		authenticationFilter.setAuthenticationFailureHandler(new BeniAuthenticationFailureHandler());
		authenticationFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationFilter;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http //@formatter:off
            .authorizeRequests()
            	.antMatchers(SLASH, WEBJARS, INDEX_JS, LINKS, ME, SIGN_UP)
            	.permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .logout()
                .logoutSuccessUrl(SLASH)
                .and()
            .csrf()
            	.disable()
            .exceptionHandling()
            	.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(SLASH)); //@formatter:on
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

	static class BeniAuthenticationFailureHandler implements AuthenticationFailureHandler {

		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException exception) throws IOException, ServletException {
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

		private static final String USERNAME_FORM_KEY = "email";

		private boolean postOnly = true;
		private int min = 5;
		private int max = 25;

		public ValidateingUsernamePasswordAuthenticationFilter() {
			setUsernameParameter(USERNAME_FORM_KEY);
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
					.orElseThrow(() -> new BadCredentialsException("Email " + EMAIL_MESSAGE));
		}

		private boolean isValidEmail(String email) {
			return email.matches(".+@.+\\..+");
		}

		private String validatePassword(String credential) {
			return Optional.ofNullable(credential).map(String::trim).filter(this::isLengthBetween5And25)
					.orElseThrow(() -> new BadCredentialsException("Password " + String.format(sizeMessage(min, max))));
		}

		private boolean isLengthBetween5And25(String string) {
			return string.length() >= min && string.length() <= max;
		}

	}

	@Component
	public static class SecurityEvaluationContextExtension extends EvaluationContextExtensionSupport {

		@Override
		public String getExtensionId() {
			return "security";
		}

		@Override
		public SecurityExpressionRoot getRootObject() {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			return new SecurityExpressionRoot(authentication) {
			};
		}
	}
}