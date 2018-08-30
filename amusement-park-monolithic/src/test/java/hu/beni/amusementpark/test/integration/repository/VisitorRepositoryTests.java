package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class VisitorRepositoryTests extends AbstractStatementCounterTests {

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private VisitorRepository visitorRepository;

	private AmusementPark amusementPark;
	private Machine machine;
	private Visitor visitor;
	private Long visitorId;

	@Before
	public void setUp() {
		amusementPark = amusementParkRepository.save(ValidEntityFactory.createAmusementParkWithAddress());
		machine = ValidEntityFactory.createMachine();
		machine.setAmusementPark(amusementPark);
		machine = machineRepository.save(machine);
		reset();
	}

	@Test
	public void test() {
		save();

		saveAll();

		findByUsername();

		countByMachineId();

		countByAmusementParkId();

		findByMachineIdAndVisitorId();

		findByAmusementParkIdAndVisitorId();

		findById();

		findAll();

		deleteById();

		deleteAll();
	}

	private void save() {
		Visitor visitorBeforeSave = createVisitorSetAmusementParkAndMachine();
		visitor = visitorRepository.save(visitorBeforeSave);
		visitorId = visitor.getId();
		assertNotNull(visitorId);
		assertEquals(visitorBeforeSave, visitor);
		assertTrue(visitor.getDateOfSignUp().isBefore(LocalDateTime.now()));
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private Visitor createVisitorSetAmusementParkAndMachine() {
		Visitor visitor = createVisitor();
		visitor.setAmusementPark(amusementPark);
		visitor.setMachine(machine);
		String username = visitor.getUsername() + Math.random();
		visitor.setUsername(username.substring(0, username.length() > 25 ? 25 : username.length()));
		return visitor;
	}

	private void saveAll() {
		visitorRepository.saveAll(
				Arrays.asList(createVisitorSetAmusementParkAndMachine(), createVisitorSetAmusementParkAndMachine()));
		insert += 2;
		incrementSelectIfOracleDBProfileActive(2);
		assertStatements();
	}

	private void findByUsername() {
		assertEquals(visitor.getSpendingMoney(),
				visitorRepository.findByUsername(visitor.getUsername()).get().getSpendingMoney());
		select++;
		assertStatements();
	}

	private void countByMachineId() {
		assertEquals(3, visitorRepository.countByMachineId(machine.getId()).longValue());
		select++;
		assertStatements();
	}

	private void countByAmusementParkId() {
		assertEquals(3, visitorRepository.countByAmusementParkId(amusementPark.getId()).longValue());
		select++;
		assertStatements();
	}

	private void findByMachineIdAndVisitorId() {
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(visitor, "visitor"));
		assertEquals(visitor, visitorRepository.findByMachineIdAndVisitorId(machine.getId(), visitorId).get());
		select++;
		assertStatements();
	}

	private void findByAmusementParkIdAndVisitorId() {
		assertEquals(visitor,
				visitorRepository.findByAmusementParkIdAndVisitorId(amusementPark.getId(), visitorId).get());
		select++;
		assertStatements();
	}

	private void findById() {
		assertEquals(visitor, visitorRepository.findById(visitorId).get());
		select++;
		assertStatements();
	}

	private void findAll() {
		assertTrue(visitorRepository.findAll().contains(visitor));
		select++;
		assertStatements();
	}

	private void deleteById() {
		visitorRepository.deleteById(visitorId);
		select++;
		delete++;
		assertStatements();
	}

	private void deleteAll() {
		visitorRepository.deleteAll();
		select++;
		delete += 3;
		assertStatements();
	}

}
