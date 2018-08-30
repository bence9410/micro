package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class VisitorServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private MachineRepository machineRepository;

	private AmusementPark amusementPark;
	private Long amusementParkId;

	private Machine machine;
	private Long machineId;

	private Visitor visitor;
	private Long visitorId;

	@Before
	public void setUp() {
		amusementPark = amusementParkRepository.save(createAmusementParkWithAddress());
		amusementParkId = amusementPark.getId();
		machine = createMachine();
		machine.setAmusementPark(amusementPark);
		machine = machineRepository.save(machine);
		machineId = machine.getId();
		reset();
	}

	@Test
	public void test() {
		signUp();

		findByUsername();

		findOne();

		findAll();

		enterPark();

		getOnMachine();

		getOffMachine();

		leavePark();

		assertSpendingMoneyChangedCorrectly();
	}

	private void signUp() {
		Visitor visitorBeforeSignUp = createVisitor();
		visitor = visitorService.signUp(visitorBeforeSignUp);
		visitorId = visitor.getId();
		assertNotNull(visitorId);
		assertEquals(visitorBeforeSignUp, visitor);
		assertTrue(visitor.getDateOfSignUp().isBefore(LocalDateTime.now()));
		insert++;
		select++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private void findByUsername() {
		assertEquals(visitor.getSpendingMoney(),
				visitorService.findByUsername(visitor.getUsername()).getSpendingMoney());
		select++;
		assertStatements();
	}

	private void findOne() {
		assertEquals(visitor, visitorService.findOne(visitorId));
		select++;
		assertStatements();
	}

	private void findAll() {
		assertTrue(visitorService.findAll(Pageable.unpaged()).getContent().contains(visitor));
		select++;
		assertStatements();
	}

	private void enterPark() {
		Visitor inParkVisitor = visitorService.enterPark(amusementParkId, visitorId);
		assertEquals(visitor.getSpendingMoney() - amusementPark.getEntranceFee(),
				inParkVisitor.getSpendingMoney().longValue());
		assertEquals(VisitorState.REST, inParkVisitor.getState());
		assertEquals(amusementParkId, inParkVisitor.getAmusementPark().getId());
		select += 4;
		update += 2;
		insert++;
		assertStatements();
		assertEquals(amusementPark.getCapital() + amusementPark.getEntranceFee(),
				amusementParkRepository.findById(amusementParkId).get().getCapital().longValue());
		select++;
		assertStatements();
	}

	private void getOnMachine() {
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(visitor, "visitor"));
		Visitor onMachineVisitor = visitorService.getOnMachine(amusementParkId, machineId, visitorId);
		assertEquals(visitor.getSpendingMoney() - amusementPark.getEntranceFee() - machine.getTicketPrice(),
				onMachineVisitor.getSpendingMoney().longValue());
		assertEquals(VisitorState.ON_MACHINE, onMachineVisitor.getState());
		assertEquals(machineId, onMachineVisitor.getMachine().getId());
		select += 3;
		update += 2;
		assertStatements();
		assertEquals(amusementPark.getCapital() + amusementPark.getEntranceFee() + machine.getTicketPrice(),
				amusementParkRepository.findById(amusementParkId).get().getCapital().longValue());
		select++;
		assertStatements();
	}

	private void getOffMachine() {
		Visitor offMachineVisitor = visitorService.getOffMachine(machineId, visitorId);
		assertNull(offMachineVisitor.getMachine());
		assertEquals(VisitorState.REST, offMachineVisitor.getState());
		select++;
		update++;
		assertStatements();
	}

	private void leavePark() {
		Visitor leftParkVisitor = visitorService.leavePark(amusementParkId, visitorId);
		assertNull(leftParkVisitor.getAmusementPark());
		assertNull(leftParkVisitor.getState());
		select++;
		update++;
		assertStatements();
	}

	private void assertSpendingMoneyChangedCorrectly() {
		assertEquals(visitor.getSpendingMoney() - amusementPark.getEntranceFee() - machine.getTicketPrice(),
				visitorService.findOne(visitorId).getSpendingMoney().longValue());
		select++;
		assertStatements();
	}

}
