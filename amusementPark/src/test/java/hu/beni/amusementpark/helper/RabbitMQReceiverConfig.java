package hu.beni.amusementpark.helper;

import static hu.beni.amusementpark.constants.RabbitMQConstants.QUEUE_NAME;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import hu.beni.amusementpark.entity.AmusementPark;
import lombok.extern.slf4j.Slf4j;

@Configuration
public class RabbitMQReceiverConfig {

	@Bean
	public ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory();
	}

	@Bean
	public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QUEUE_NAME);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	public Receiver receiver() {
		return new Receiver();
	}

	@Bean
	public MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	@Slf4j
	static class Receiver {

		public void receiveMessage(AmusementPark amusementPark) {
			log.info(amusementPark.toString());
		}

	}
}
