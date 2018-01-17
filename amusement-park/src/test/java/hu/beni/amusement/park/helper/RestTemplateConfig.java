package hu.beni.amusement.park.helper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.getInterceptors().add(getLoggingInterceptor());
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
}
