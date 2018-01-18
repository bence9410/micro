package hu.beni.amusementpark.archive.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.entity.AmusementPark;
import lombok.RequiredArgsConstructor;

import static hu.beni.amusementpark.constants.RabbitMQConstants.*;

@Component
@Profile(RABBIT_MQ_PROFILE_NAME)
@RequiredArgsConstructor
public class RabbitMQArchiveSender implements ArchiveSender {

	private final RabbitTemplate rabbitTemplate;
	
	@Override
	public void sendToArchive(AmusementPark amusementPark) {
		rabbitTemplate.convertAndSend(QUEUE_NAME, amusementPark);
	}	
}
