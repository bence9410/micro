package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.RabbitMQConstants.ARCHIVE_EXCHANGE_NAME;
import static hu.beni.amusementpark.constants.RabbitMQConstants.ARCHIVE_QUEUE_NAME;
import static hu.beni.amusementpark.constants.RabbitMQConstants.STATISTICS_EXCHANGE_NAME;
import static hu.beni.amusementpark.constants.RabbitMQConstants.STATISTICS_QUEUE_NAME;
import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@Profile(ORACLE_DB)
@EnableAsync
public class RabbitMQConfig {

	@Bean
	public Queue archiveQueue() {
		return new Queue(ARCHIVE_QUEUE_NAME, false);
	}

	@Bean
	public TopicExchange archiveExchange() {
		return new TopicExchange(ARCHIVE_EXCHANGE_NAME);
	}

	@Bean
	public Binding archiveAmusementParkBinding() {
		return BindingBuilder.bind(archiveQueue()).to(archiveExchange()).with(ARCHIVE_QUEUE_NAME);
	}

	@Bean
	public Queue statisticsQueue() {
		return new Queue(STATISTICS_QUEUE_NAME, false);
	}

	@Bean
	public TopicExchange statisticsExchange() {
		return new TopicExchange(STATISTICS_EXCHANGE_NAME);
	}

	@Bean
	public Binding statisticsBinding() {
		return BindingBuilder.bind(statisticsQueue()).to(statisticsExchange()).with(STATISTICS_QUEUE_NAME);
	}
}
