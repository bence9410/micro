package hu.beni.gateway.config;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/index.js", "/webjars/**", "/uaa/oauth/authorize",
                		"/uaa/login", "/uaa/oauth/token", "/uaa/user")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .logout().logoutSuccessUrl("/").and()
            .csrf().disable();
    }
	
	@Bean
	public OAuth2ClientContextFilter oauth2ClientContextFilter() {
		return new  OAuth2ClientContextFilter() {
		    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	
		    @Override
		    protected void redirectUser(UserRedirectRequiredException e, HttpServletRequest request,
		                                HttpServletResponse response) throws IOException {
		        String redirectUri = e.getRedirectUri();
		        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri);
		        Map<String, String> requestParams = e.getRequestParams();
		        for (Map.Entry<String, String> param : requestParams.entrySet()) {
		            builder.queryParam(param.getKey(), param.getValue());
		        }
	
		        if (e.getStateKey() != null) {
		            builder.queryParam("state", e.getStateKey());
		        }
	
		        String url = getBaseUrl(request) + builder.build().encode().toUriString();
		        this.redirectStrategy.sendRedirect(request, response, url);
		    }
	
		    private String getBaseUrl(HttpServletRequest request) {
		        StringBuffer url = request.getRequestURL();
		        return  url.substring(0, url.length() - request.getRequestURI().length() + request.getContextPath().length());
		    }
		};
	}

}
