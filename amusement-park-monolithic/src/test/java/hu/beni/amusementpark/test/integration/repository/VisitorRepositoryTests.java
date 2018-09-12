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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

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

	private int num;

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

		findByEmail();

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
		Visitor v = createVisitor();
		v.setAmusementPark(amusementPark);
		v.setMachine(machine);
		v.setEmail(v.getEmail().replace('4', Integer.toString(num++).charAt(0)));
		return v;
	}

	private void saveAll() {
		visitorRepository.saveAll(
				Arrays.asList(createVisitorSetAmusementParkAndMachine(), createVisitorSetAmusementParkAndMachine()));
		insert += 2;
		incrementSelectIfOracleDBProfileActive(2);
		assertStatements();
	}

	private void findByEmail() {
		assertEquals(visitor.getSpendingMoney(),
				visitorRepository.findByEmail(visitor.getEmail()).get().getSpendingMoney());
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
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(new User(visitor.getEmail(), visitor.getPassword(),
						Arrays.asList(new SimpleGrantedAuthority(visitor.getAuthority()))), null));
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
