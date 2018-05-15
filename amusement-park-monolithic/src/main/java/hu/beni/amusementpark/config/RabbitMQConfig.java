package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.RabbitMQConstants.EXCHANGE_NAME;
import static hu.beni.amusementpark.constants.RabbitMQConstants.QUEUE_NAME;
import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(ORACLE_DB)
public class RabbitMQConfig {

	@Bean
	public Queue queue() {
		return new Queue(QUEUE_NAME, false);
	}

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(QUEUE_NAME);
	}

}
