package hu.beni.tester.archive;

import java.util.concurrent.CountDownLatch;

import org.springframework.stereotype.Component;

import hu.beni.clientsupport.dto.ArchiveAmusementParkDTO;
import hu.beni.tester.properties.ApplicationProperties;
import lombok.Getter;

@Getter
@Component
public class ArchiveReceiver {

	private final CountDownLatch countDownLatch;

	public ArchiveReceiver(ApplicationProperties properties) {
		countDownLatch = new CountDownLatch(properties.getNumberOf().getAmusementParks());
	}

	public void receive(ArchiveAmusementParkDTO archiveAmusementParkDTO) {
		countDownLatch.countDown();
	}

}
