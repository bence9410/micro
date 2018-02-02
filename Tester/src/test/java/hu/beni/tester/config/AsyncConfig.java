package hu.beni.tester.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {
	
	@Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        //restTemplate.getInterceptors().add(getLoggingInterceptor());
        return restTemplate;
    }

    private ClientHttpRequestInterceptor getLoggingInterceptor() {
        return (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            log.info("Request: { URL: {}, Method: {}, Body: {} }", request.getURI(), request.getMethod(), new String(body));
            ClientHttpResponse response = execution.execute(request, body);
            log.info("Response: { Status: {}, Body: {} }", response.getStatusCode().toString(), 
            		new String(FileCopyUtils.copyToByteArray(response.getBody())));
            return response;
        };
    }
	
	@Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build()
        		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
	
	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.initialize();
		return executor;
	}
}