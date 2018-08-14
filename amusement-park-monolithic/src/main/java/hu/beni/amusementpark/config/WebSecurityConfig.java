package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.AuthenticationConstants.ADMIN;
import static hu.beni.amusementpark.constants.AuthenticationConstants.ADMIN_LOWER_CASE;
import static hu.beni.amusementpark.constants.AuthenticationConstants.PASS;
import static hu.beni.amusementpark.constants.AuthenticationConstants.VISITOR;
import static hu.beni.amusementpark.constants.AuthenticationConstants.VISITOR_LOWER_CASE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("deprecation")
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	public WebSecurityConfig(ObjectMapper objectMapper) {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
	}

	@Bean
	public NoOpPasswordEncoder noOpPasswordEncoder() {
		return NoOpPasswordEncoder.class.cast(NoOpPasswordEncoder.getInstance());
	}

	@Bean
	public BeniAuthenticationSuccessHandler authenticationSuccessHandler(VisitorService visitorService,
			ObjectMapper objectMapper) {
		return new BeniAuthenticationSuccessHandler(visitorService, objectMapper);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http //@formatter:off
            .authorizeRequests()
            	.antMatchers("/", "/webjars/**", "/index.js", "/links", "/user")
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

		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
			Map<String, Object> map = new HashMap<>();
			map.put("name", authentication.getName());
			map.put("authorities", authentication.getAuthorities());
			map.put("spendingMoney", visitorService.findSpendingMoneyByUsername());
			response.getWriter().println(objectMapper.writerFor(Map.class).writeValueAsString(map));
		}

	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryUserDetails = auth
				.inMemoryAuthentication();
		createUser(inMemoryUserDetails, ADMIN_LOWER_CASE, ADMIN);
		createUser(inMemoryUserDetails, VISITOR_LOWER_CASE, VISITOR);
		IntStream.range(0, 10).forEach(i -> {
			createUser(inMemoryUserDetails, ADMIN_LOWER_CASE + i, ADMIN);
			createUser(inMemoryUserDetails, VISITOR_LOWER_CASE + i, VISITOR);
		});
		IntStream.range(10, 20).forEach(i -> createUser(inMemoryUserDetails, VISITOR_LOWER_CASE + i, VISITOR));
	}

	private void createUser(InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> inMemoryUserDetails,
			String username, String role) {
		inMemoryUserDetails.withUser(username).password(PASS).roles(role);
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