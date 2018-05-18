package hu.beni.gateway.config;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableOAuth2Sso
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/index.js", "/webjars/**", "/uaa/oauth/authorize",
                		"/uaa/login", "/uaa/oauth/token")
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
		    
		    @Override
		    protected void redirectUser(UserRedirectRequiredException e, HttpServletRequest request,
		                                HttpServletResponse response) throws IOException {
		    
		    	String redirectUri = e.getRedirectUri();
		        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(redirectUri);
		        
		        for (Entry<String, String> param : e.getRequestParams().entrySet()) {
		            builder.queryParam(param.getKey(), param.getValue());
		        }
	
		        if (e.getStateKey() != null) {
		            builder.queryParam("state", e.getStateKey());
		        }
	
		        response.sendRedirect(builder.build().encode().toUriString());
		    }
	
		};
	}

}
