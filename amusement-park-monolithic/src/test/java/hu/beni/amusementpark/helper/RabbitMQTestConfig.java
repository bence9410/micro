package hu.beni.amusementpark.helper;

import static hu.beni.amusementpark.constants.RabbitMQConstants.EXCHANGE_NAME;
import static hu.beni.amusementpark.constants.RabbitMQConstants.QUEUE_NAME;
import static hu.beni.amusementpark.constants.SpringTestProfileConstants.RABBIT_MQ_TEST_CONFIG;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.archive.impl.RabbitMQArchiveSender;
import hu.beni.clientsupport.dto.ArchiveAmusementParkDTO;
import lombok.Getter;

import static org.junit.Assert.assertNotNull;

import java.util.concurrent.CountDownLatch;

@Configuration
@Profile(RABBIT_MQ_TEST_CONFIG)
public class RabbitMQTestConfig {

	@Bean
	public ArchiveSender archiveSender(RabbitTemplate rabbitTemplate) {
		return new RabbitMQArchiveSender(rabbitTemplate);
	}

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

	@Bean
	public ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		return new RabbitTemplate(connectionFactory);
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
		return new MessageListenerAdapter(receiver, "receiveArchiveAmusementParkDTO");
	}

	@Getter
	public static class Receiver {

		private final CountDownLatch countDownLatch = new CountDownLatch(1);

		public void receiveArchiveAmusementParkDTO(ArchiveAmusementParkDTO archiveAmusementParkDTO) {
			assertNotNull(archiveAmusementParkDTO);
			countDownLatch.countDown();
		}

	}
}
