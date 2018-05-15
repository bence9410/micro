package hu.beni.amusementpark.test.integration;

import static hu.beni.amusementpark.helper.ValidEntityFactory.*;
import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.VisitorService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class VisitorServiceIntegrationTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private MachineService machineService;

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Test
	public void test() {
		AmusementPark amusementPark = createAmusementParkWithAddress();
		Long amusementParkId = amusementParkService.save(amusementPark).getId();
		Integer capital = amusementPark.getCapital();
		Integer entranceFee = amusementPark.getEntranceFee();

		Machine machine = createMachine();
		Integer ticketPrice = machine.getTicketPrice();
		Long machineId = machineService.addMachine(amusementParkId, machine).getId();
		capital -= machine.getPrice();

		Visitor visitor = createVisitor();
		visitor = visitorService.signUp(visitor);
		Long visitorId = visitor.getId();
		assertNotNull(visitorId);
		assertTrue(visitor.getDateOfSignUp().isBefore(LocalDateTime.now()));

		Integer spendingMoney = visitor.getSpendingMoney();

		visitorService.enterPark(amusementParkId, visitorId);
		capital += entranceFee;
		spendingMoney -= entranceFee;
		assertEquals(capital, amusementParkService.findByIdFetchAddress(amusementParkId).getCapital());

		visitor = visitorService.findOne(visitorId);
		assertNotNull(visitor.getAmusementPark());
		assertEquals(spendingMoney, visitor.getSpendingMoney());
		assertEquals(VisitorState.REST, visitor.getState());

		visitorService.getOnMachine(amusementParkId, machineId, visitorId);
		capital += ticketPrice;
		spendingMoney -= ticketPrice;
		assertEquals(capital, amusementParkService.findByIdFetchAddress(amusementParkId).getCapital());

		visitor = visitorService.findOne(visitorId);
		assertEquals(spendingMoney, visitor.getSpendingMoney());
		assertEquals(VisitorState.ON_MACHINE, visitor.getState());

		visitorService.getOffMachine(machineId, visitorId);
		assertEquals(VisitorState.REST, visitorService.findOne(visitorId).getState());

		visitorService.leavePark(amusementParkId, visitorId);
		assertNull(visitorService.findOne(visitorId).getAmusementPark());

		amusementParkRepository.deleteById(amusementParkId);
		assertNotNull(visitorService.findOne(visitorId));
	}

}
