package hu.beni.amusementpark.test;

import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        restTemplate.getInterceptors().add(getLoggingInterceptor());
        return restTemplate;
    }

    private static ClientHttpRequestInterceptor getLoggingInterceptor() {
        return (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
            log.info("Request: { URL: {}, Method: {}, Body: {} }", request.getURI(), request.getMethod(), new String(body));
            ClientHttpResponse response = execution.execute(request, body);
            log.info("Response: { Status: {}, Body: {} }", response.getStatusCode().toString(), getResponseBodyAsString(response.getBody()));
            return response;
        };
    }

    private static String getResponseBodyAsString(InputStream inputStream) {
        byte[] body = null;
        try {
            int shouldBeAll = inputStream.available();
            //inputStream.mark(shouldBeAll);
            body = new byte[shouldBeAll];
            inputStream.read(body);
            //inputStream.reset();
        } catch (IOException e) {
            log.info("Could not log response!", e);
        }
        return new String(body);
    }

}
