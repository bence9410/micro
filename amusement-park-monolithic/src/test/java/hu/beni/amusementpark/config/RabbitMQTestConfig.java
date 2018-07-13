package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.RabbitMQConstants.ARCHIVE_QUEUE_NAME;
import static hu.beni.amusementpark.constants.RabbitMQConstants.STATISTICS_QUEUE_NAME;

import java.util.concurrent.CountDownLatch;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import hu.beni.clientsupport.dto.ArchiveAmusementParkDTO;
import hu.beni.clientsupport.dto.VisitorEnterParkEventDTO;
import hu.beni.clientsupport.dto.VisitorGetOnMachineEventDTO;
import lombok.Getter;

public class RabbitMQTestConfig {

	@Getter
	@Component
	public static class ArchiveReceiver {

		private final CountDownLatch archiveCountDownLatch = new CountDownLatch(1);
		private ArchiveAmusementParkDTO receivedArchiveAmusementParkDTO;

		@RabbitListener(queues = ARCHIVE_QUEUE_NAME)
		public void receiveArchiveAmusementParkDTO(ArchiveAmusementParkDTO dto) {
			receivedArchiveAmusementParkDTO = dto;
			archiveCountDownLatch.countDown();
		}
	}

	@Getter
	@Component
	@RabbitListener(queues = STATISTICS_QUEUE_NAME)
	public static class StatisticsReceiver {

		private final CountDownLatch enterParkCountDownLatch = new CountDownLatch(1);
		private final CountDownLatch getOnMachineCountDownLatch = new CountDownLatch(1);
		private VisitorEnterParkEventDTO visitorEnterParkEventDTO;
		private VisitorGetOnMachineEventDTO visitorGetOnMachineEventDTO;

		@RabbitHandler
		public void reveiveVisitorStatistics(VisitorEnterParkEventDTO dto) {
			visitorEnterParkEventDTO = dto;
			enterParkCountDownLatch.countDown();
		}

		@RabbitHandler
		public void reveiVisitorStatistics(VisitorGetOnMachineEventDTO dto) {
			visitorGetOnMachineEventDTO = dto;
			getOnMachineCountDownLatch.countDown();
		}

	}
}
