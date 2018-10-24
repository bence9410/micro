package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
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
	private String visitorEmail;

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

		findByEmail();

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
		visitorEmail = visitor.getEmail();
		assertEquals(visitorBeforeSignUp, visitor);
		assertTrue(visitor.getDateOfSignUp().isBefore(LocalDateTime.now()));
		insert++;
		select += 2;
		assertStatements();
	}

	private void findByEmail() {
		assertEquals(visitor.getSpendingMoney(), visitorService.findByEmail(visitor.getEmail()).getSpendingMoney());
		select++;
		assertStatements();
	}

	private void findOne() {
		assertEquals(visitor, visitorService.findOne(visitorEmail));
		select++;
		assertStatements();
	}

	private void findAll() {
		assertTrue(visitorService.findAll(Pageable.unpaged()).getContent().contains(visitor));
		select++;
		assertStatements();
	}

	private void enterPark() {
		Visitor inParkVisitor = visitorService.enterPark(amusementParkId, visitorEmail);
		assertEquals(visitor.getSpendingMoney() - amusementPark.getEntranceFee(),
				inParkVisitor.getSpendingMoney().longValue());
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
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(new User(visitor.getEmail(), visitor.getPassword(),
						Arrays.asList(new SimpleGrantedAuthority(visitor.getAuthority()))), null));
		Visitor onMachineVisitor = visitorService.getOnMachine(amusementParkId, machineId, visitorEmail);
		assertEquals(visitor.getSpendingMoney() - amusementPark.getEntranceFee() - machine.getTicketPrice(),
				onMachineVisitor.getSpendingMoney().longValue());
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
		Visitor offMachineVisitor = visitorService.getOffMachine(machineId, visitorEmail);
		assertNull(offMachineVisitor.getMachine());
		select++;
		update++;
		assertStatements();
	}

	private void leavePark() {
		Visitor leftParkVisitor = visitorService.leavePark(amusementParkId, visitorEmail);
		assertNull(leftParkVisitor.getAmusementPark());
		select++;
		update++;
		assertStatements();
	}

	private void assertSpendingMoneyChangedCorrectly() {
		assertEquals(visitor.getSpendingMoney() - amusementPark.getEntranceFee() - machine.getTicketPrice(),
				visitorService.findOne(visitorEmail).getSpendingMoney().longValue());
		select++;
		assertStatements();
	}

}
