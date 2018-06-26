package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class MachineRepositoryTests extends AbstractStatementCounterTests {

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Autowired
	private MachineRepository machineRepository;

	private AmusementPark amusementPark;
	private Long amusementParkId;
	private Machine machine;

	@Before
	public void setUp() {
		machineRepository.deleteAll();
		amusementPark = amusementParkRepository.save(createAmusementParkWithAddress());
		amusementParkId = amusementPark.getId();
		reset();
	}

	@Test
	public void test() {
		assertStatements();

		save();

		sumAreaByAmusementParkId();

		findByAmusementParkIdAndMachineId();

		findAllByAmusementParkId();

		findById();

		findAll();

		deleteById();
	}

	private void save() {
		machine = createMachine();
		machine.setAmusementPark(amusementPark);
		machine = machineRepository.save(machine);
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private void sumAreaByAmusementParkId() {
		assertEquals(machine.getSize().intValue(),
				machineRepository.sumAreaByAmusementParkId(amusementParkId).get().intValue());
		select++;
		assertStatements();
	}

	private void findByAmusementParkIdAndMachineId() {
		assertEquals(machine,
				machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machine.getId()).get());
		select++;
		assertStatements();
	}

	private void findAllByAmusementParkId() {
		assertEquals(machine, machineRepository.findAllByAmusementParkId(amusementParkId).get(0));
		select++;
		assertStatements();
	}

	private void findById() {
		assertEquals(machine, machineRepository.findById(machine.getId()).get());
		select++;
		assertStatements();
	}

	private void findAll() {
		assertEquals(machine, machineRepository.findAll().get(0));
		select++;
		assertStatements();
	}

	private void deleteById() {
		machineRepository.deleteById(machine.getId());
		select++;
		delete++;
		assertStatements();
	}

}
