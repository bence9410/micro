package hu.beni.tester.archive;

import static hu.beni.tester.TesterApplicationTests.NUMBER_OF_ADMINS;
import static hu.beni.tester.constant.Constants.NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN;

import java.util.concurrent.CountDownLatch;

import org.springframework.stereotype.Component;

import hu.beni.clientsupport.dto.ArchiveAmusementParkDTO;
import lombok.Getter;

@Getter
@Component
public class ArchiveReceiver {

	private final CountDownLatch countDownLatch = new CountDownLatch(
			NUMBER_OF_ADMINS * NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN);

	public void receive(ArchiveAmusementParkDTO archiveAmusementParkDTO) {
		countDownLatch.countDown();
	}

}
