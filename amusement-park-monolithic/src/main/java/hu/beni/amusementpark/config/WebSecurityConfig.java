package hu.beni.amusementpark.config;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
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

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http //@formatter:off
            .authorizeRequests()
            	.antMatchers("/", "/webjars/**", "/index.js", "/links", "/user", "/signUp")
            	.permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .formLogin()
            	.loginPage("/")
            	.loginProcessingUrl("/login")
                .successHandler(authenticationSuccessHandler(null, null))
                .and()
            .logout()
                .logoutSuccessUrl("/")
                .and()
            .csrf()
            	.disable(); //@formatter:on
	}

	@RequiredArgsConstructor
	static class BeniAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

		private final VisitorService visitorService;
		private final ObjectMapper objectMapper;
		private final VisitorMapper visitorMapper;

		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
			Visitor visitor = visitorService.findByUsernameReadAuthorityAndSpendingMoney();
			visitor.setUsername(authentication.getName());
			response.getWriter().println(objectMapper.writeValueAsString(visitorMapper.toResource(visitor)));
		}

	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService(null)).passwordEncoder(passwordEncoder());
	}

	@RequiredArgsConstructor
	static class UserDetailsServiceImpl implements UserDetailsService {

		private final VisitorRepository visitorRepository;

		@Override
		public UserDetails loadUserByUsername(String username) {
			Visitor visitor = visitorRepository.findByUsernameReadOnlyPasswordAndAuthority(username)
					.orElseThrow(() -> new UsernameNotFoundException(
							String.format("Could not find user with username: %s.", username)));
			return new User(username, visitor.getPassword(),
					Arrays.asList(new SimpleGrantedAuthority(visitor.getAuthority())));
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