package hu.beni.amusementpark.test.rabbitmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import hu.beni.amusementpark.config.RabbitMQTestConfig.StatisticsReceiver;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.clientsupport.dto.VisitorEnterParkEventDTO;
import hu.beni.clientsupport.dto.VisitorGetOnMachineEventDTO;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RabbitMQStatisticsSenderTests extends AbstractRabbitMQTests {

	@Autowired
	private StatisticsReceiver receiver;

	@Test
	public void test() throws InterruptedException {
		AmusementPark amusementPark = amusementParkService.save(ValidEntityFactory.createAmusementParkWithAddress());
		Machine machine = machineService.addMachine(amusementPark.getId(), ValidEntityFactory.createMachine());
		Visitor visitor = visitorService.signUp(ValidEntityFactory.createVisitor());
		String visitorEmail = visitor.getEmail();

		visitorService.enterPark(amusementPark.getId(), visitorEmail);
		assertEnterParkEventReceivedAndEquals(
				new VisitorEnterParkEventDTO(amusementPark.getId(), visitorEmail, amusementPark.getEntranceFee()));

		visitorService.getOnMachine(amusementPark.getId(), machine.getId(), visitorEmail);
		assertGetOnMachineEventReceivedAndEquals(new VisitorGetOnMachineEventDTO(amusementPark.getId(), visitorEmail,
				machine.getTicketPrice(), machine.getId()));
	}

	private void assertEnterParkEventReceivedAndEquals(VisitorEnterParkEventDTO expected) throws InterruptedException {
		assertTrue(receiver.getEnterParkCountDownLatch().await(5, TimeUnit.SECONDS));
		assertEquals(expected, receiver.getVisitorEnterParkEventDTO());
	}

	private void assertGetOnMachineEventReceivedAndEquals(VisitorGetOnMachineEventDTO expected)
			throws InterruptedException {
		assertTrue(receiver.getGetOnMachineCountDownLatch().await(5, TimeUnit.SECONDS));
		assertEquals(expected, receiver.getVisitorGetOnMachineEventDTO());
	}

}
