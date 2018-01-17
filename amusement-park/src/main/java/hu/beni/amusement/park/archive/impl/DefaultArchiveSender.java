package hu.beni.amusement.park.archive.impl;

import static hu.beni.amusement.park.constants.ErrorMessageConstants.NO_ARCHIVE_SEND_TYPE;
import static hu.beni.amusement.park.constants.RabbitMQConstants.RABBIT_MQ_PROFILE_NAME;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.beni.amusement.park.archive.ArchiveSender;
import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.exception.AmusementParkException;

@Component
@Profile("!"+RABBIT_MQ_PROFILE_NAME)
public class DefaultArchiveSender implements ArchiveSender{
	
	@Override
	public void sendToArchive(AmusementPark amusementPark) {
		throw new AmusementParkException(NO_ARCHIVE_SEND_TYPE);
	}

}
