package hu.beni.amusementpark.archive.impl;

import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.beni.amusementpark.archive.ArchiveSender;
import hu.beni.amusementpark.entity.AmusementPark;

@Component
@Profile(ORACLE_DB)
public class OracleDBArchiveSender implements ArchiveSender {

	@Override
	public void sendToArchive(AmusementPark amusementPark) {
		// do nothing
	}
}
