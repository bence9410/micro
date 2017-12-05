package hu.beni.amusementpark.archive.impl;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.exception.AmusementParkException;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_ARCHIVE_SEND_TYPE;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static hu.beni.amusementpark.constants.RabbitMQConstants.RABBIT_MQ_PROFILE_NAME;

@Component
@Profile("!"+RABBIT_MQ_PROFILE_NAME)
public class DefaultArchiveSender implements ArchiveSender{
	
	@Override
	public void sendToArchive(AmusementPark amusementPark) {
		throw new AmusementParkException(NO_ARCHIVE_SEND_TYPE);
	}

}
