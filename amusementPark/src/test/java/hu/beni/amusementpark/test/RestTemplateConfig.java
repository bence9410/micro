package hu.beni.amusementpark.test;

import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.getInterceptors().add(getLoggingInterceptor());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {

            @Override
            protected boolean hasError(HttpStatus statusCode) {
                return !HttpStatus.Series.SUCCESSFUL.equals(statusCode.series()) && !HttpStatus.I_AM_A_TEAPOT.equals(statusCode);
            }

        });
        return restTemplate;
    }

    private ClientHttpRequestInterceptor getLoggingInterceptor() {
        return (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            log.info("Request: { URL: {}, Method: {}, Body: {} }", request.getURI(), request.getMethod(), new String(body));
            ClientHttpResponse response = execution.execute(request, body);
            log.info("Response: { Status: {}, Body: {} }", response.getStatusCode().toString(), getResponseBodyAsString(response.getBody()));
            return response;
        };
    }

    private String getResponseBodyAsString(InputStream inputStream) {
        byte[] body = null;
        try {
            body = new byte[inputStream.available()];
            inputStream.read(body);
        } catch (IOException e) {
            log.info("Could not log response!", e);
        }
        return new String(body);
    }
}
