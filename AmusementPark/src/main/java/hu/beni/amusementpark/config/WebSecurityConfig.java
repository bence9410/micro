package hu.beni.amusementpark.config;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.stereotype.Component;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@SuppressWarnings("deprecation")
	@Bean
	public NoOpPasswordEncoder noOpPasswordEncoder() {
		return NoOpPasswordEncoder.class.cast(NoOpPasswordEncoder.getInstance());
	}
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/docker", "/index.js")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .formLogin()
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/home.html", true)
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .and()
            .csrf()
            	.disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password("pass").roles("ADMIN").and()
                .withUser("user").password("pass").roles("USER");
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
		    return new SecurityExpressionRoot(authentication) {};
		}
    }
}