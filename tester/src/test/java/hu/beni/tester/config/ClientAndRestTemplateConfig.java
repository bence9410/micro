package hu.beni.tester.config;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import hu.beni.clientsupport.Client;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequiredArgsConstructor
public class ClientAndRestTemplateConfig {

	private final ObjectMapper mapper;

	@PostConstruct
	public void init() {
		mapper.registerModule(new JavaTimeModule());
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public Client client(RestTemplate restTemplate) {
		return new Client(restTemplate);
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public RestTemplate restTemplate() {
		return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
	}

}
