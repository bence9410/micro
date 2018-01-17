package hu.beni.amusement.park.helper;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import hu.beni.amusement.park.entity.AmusementPark;
import lombok.Getter;

import static hu.beni.amusement.park.constants.RabbitMQConstants.QUEUE_NAME;
import static hu.beni.amusement.park.constants.RabbitMQConstants.RABBIT_MQ_PROFILE_NAME;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CountDownLatch;

@Configuration
@Profile(RABBIT_MQ_PROFILE_NAME)
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

	@Getter
	public static class Receiver {
		
		private final CountDownLatch countDownLatch = new CountDownLatch(1);

		public void receiveMessage(AmusementPark amusementPark) {
			assertNotNull(amusementPark);
			countDownLatch.countDown();
		}

	}
}
