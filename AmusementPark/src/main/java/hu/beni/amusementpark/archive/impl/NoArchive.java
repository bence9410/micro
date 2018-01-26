package hu.beni.amusementpark.archive.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.entity.AmusementPark;

@Component
@Profile("oracleDB")
public class NoArchive implements ArchiveSender{

	@Override
	public void sendToArchive(AmusementPark amusementPark) {
		
	}

}
