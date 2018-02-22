package hu.beni.gateway.config;

import org.springframework.cloud.netflix.zuul.filters.post.LocationRewriteFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
	
	@Bean
	public LocationRewriteFilter locationRewriteFilter() {
		return new LocationRewriteFilter();
	}

}
