package hu.beni.amusement.park.archive.impl;

import static hu.beni.amusement.park.constants.RabbitMQConstants.*;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.beni.amusement.park.archive.ArchiveSender;
import hu.beni.amusement.park.entity.AmusementPark;
import lombok.RequiredArgsConstructor;

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
