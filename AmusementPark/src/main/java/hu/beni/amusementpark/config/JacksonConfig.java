package hu.beni.amusementpark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    	return builder.createXmlMapper(false).failOnEmptyBeans(false).build()
        		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
}
