package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.constants.SpringProfileConstants.ORACLE_DB;
import static hu.beni.amusementpark.helper.MySQLStatementCountValidator.assertSQLStatements;
import static hu.beni.amusementpark.helper.MySQLStatementCountValidator.reset;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class MachineRepositoryTests {

	@Autowired
	private Environment environment;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private MachineRepository machineRepository;

	private long insert;
	private long select;
	private long update;
	private long delete;

	private AmusementPark amusementPark;
	private Long amusementParkId;
	private Long machineId;

	private void assertStatements() {
		assertSQLStatements(select, insert, update, delete);
	}

	@Before
	public void setUp() {
		amusementPark = amusementParkRepository.save(createAmusementParkWithAddress());
		amusementParkId = amusementPark.getId();
		reset();
	}

	@Test
	public void test() {
		assertStatements();

		if (Arrays.asList(environment.getActiveProfiles()).contains(ORACLE_DB)) {
			select++;
		}

		save();

		sumAreaByAmusementParkId();

		findByAmusementParkIdAndMachineId();

		findAllByAmusementParkId();

		findById();

		findAll();
	}

	private void save() {
		Machine machine = createMachine();
		machine.setAmusementPark(amusementPark);
		machineId = machineRepository.save(machine).getId();
		insert++;
		assertStatements();
	}

	private void sumAreaByAmusementParkId() {
		machineRepository.sumAreaByAmusementParkId(amusementParkId);
		select++;
		assertStatements();
	}

	private void findByAmusementParkIdAndMachineId() {
		machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		select++;
		assertStatements();
	}

	private void findAllByAmusementParkId() {
		machineRepository.findAllByAmusementParkId(amusementParkId);
		select++;
		assertStatements();
	}

	private void findById() {
		machineRepository.findById(machineId);
		select++;
		assertStatements();
	}

	private void findAll() {
		machineRepository.findAll();
		select++;
		assertStatements();
	}

}
