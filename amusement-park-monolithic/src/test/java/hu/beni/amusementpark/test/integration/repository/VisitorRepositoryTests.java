package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;
import static hu.beni.amusementpark.helper.MySQLStatementCountValidator.assertSQLStatements;
import static hu.beni.amusementpark.helper.MySQLStatementCountValidator.reset;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class VisitorRepositoryTests {

	@Autowired
	private Environment environment;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private VisitorRepository visitorRepository;

	private long insert;
	private long select;
	private long update;
	private long delete;

	private AmusementPark amusementPark;
	private Machine machine;
	private Visitor visitor;
	private Long visitorId;

	private void assertStatements() {
		assertSQLStatements(select, insert, update, delete);
	}

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
		assertStatements();

		if (Arrays.asList(environment.getActiveProfiles()).contains(ORACLE_DB)) {
			select++;
		}

		save();

		findSpendingMoneyByUserName();

		countByMachineId();

		countByAmusementParkId();

		findByMachineIdAndVisitorId();

		findByAmusementParkIdAndVisitorId();

		findById();

		findAll();

		deleteById();
	}

	private void save() {
		visitor = createVisitor();
		visitor.setAmusementPark(amusementPark);
		visitor.setMachine(machine);
		visitor = visitorRepository.save(visitor);
		visitorId = visitor.getId();
		insert++;
		assertStatements();
	}

	private void findSpendingMoneyByUserName() {
		SecurityContextHolder.getContext()
				.setAuthentication(new UsernamePasswordAuthenticationToken(visitor, "visitor"));
		assertEquals(visitor.getSpendingMoney(), visitorRepository.findSpendingMoneyByUsername().get());
		select++;
		assertStatements();
	}

	private void countByMachineId() {
		assertEquals(1, visitorRepository.countByMachineId(machine.getId()).longValue());
		select++;
		assertStatements();
	}

	private void countByAmusementParkId() {
		assertEquals(1, visitorRepository.countByAmusementParkId(amusementPark.getId()).longValue());
		select++;
		assertStatements();
	}

	private void findByMachineIdAndVisitorId() {
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
		assertEquals(visitor, visitorRepository.findAll().get(0));
		select++;
		assertStatements();
	}

	private void deleteById() {
		visitorRepository.deleteById(visitorId);
		select++;
		delete++;
		assertStatements();
	}

}
