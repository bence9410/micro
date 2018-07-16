package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.SpringProfileConstants.RABBIT_MQ;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Profile(RABBIT_MQ)
@EnableAsync
public class RabbitMQConfig {

	@Bean
	public Queue archiveQueue() {
		return new AnonymousQueue();
	}

	@Bean
	public Queue statisticsQueue() {
		return new AnonymousQueue();
	}

}
