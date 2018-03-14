package hu.beni.zooui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfig {

	@Bean
	public RemoteTokenServices remoteTokenServices(Environment environment) {
		String tokenUrl;
		if (environment.getActiveProfiles().length == 0) {
			tokenUrl = "localhost:9999";
		} else {
			tokenUrl = "oauth2";
		}
		RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		remoteTokenServices.setCheckTokenEndpointUrl("http://" + tokenUrl + "/uaa/oauth/check_token");
		remoteTokenServices.setClientId("beni");
		remoteTokenServices.setClientSecret("benisecret");
		return remoteTokenServices;
	}

}
