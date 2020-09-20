package hu.beni.visitor.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig {

	@Bean
	@Profile("default")
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public RemoteTokenServices remoteTokenServices(Environment environment) {
		RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		if (environment.getActiveProfiles().length == 0) {
			remoteTokenServices.setRestTemplate(restTemplate());
		}
		remoteTokenServices.setCheckTokenEndpointUrl("http://oauth2/uaa/oauth/check_token");
		remoteTokenServices.setClientId("beni");
		remoteTokenServices.setClientSecret("benisecret");
		return remoteTokenServices;
	}

}
