package hu.beni.amusementpark.archive.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.dto.ArchiveAmusementParkDTO;
import lombok.RequiredArgsConstructor;

import static hu.beni.amusementpark.constants.RabbitMQConstants.*;
import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;

@Component
@Profile(ORACLE_DB)
@RequiredArgsConstructor
public class RabbitMQArchiveSender implements ArchiveSender {

	private final RabbitTemplate rabbitTemplate;
	
	@Override
	public void sendToArchive(AmusementPark amusementPark) {
		rabbitTemplate.convertAndSend(QUEUE_NAME, convert(amusementPark));
	}	
	
	private ArchiveAmusementParkDTO convert(AmusementPark amusementPark) {
		return ArchiveAmusementParkDTO.builder()
				.name(amusementPark.getName()).build();
				
	}
}
