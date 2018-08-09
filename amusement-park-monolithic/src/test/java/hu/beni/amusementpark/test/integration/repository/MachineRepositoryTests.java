package hu.beni.amusementpark.test.integration.repository;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

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
		amusementPark = amusementParkRepository.save(createAmusementParkWithAddress());
		amusementParkId = amusementPark.getId();
		reset();
	}

	@Test
	public void test() {
		save();

		saveAll();

		sumAreaByAmusementParkId();

		findByAmusementParkIdAndMachineId();

		findAllByAmusementParkId();

		findById();

		findAll();

		deleteById();

		deleteAll();
	}

	private void save() {
		Machine machineBeforeSave = createMachineSetAmusementPark();
		machine = machineRepository.save(machineBeforeSave);
		assertNotNull(machine.getId());
		assertEquals(machineBeforeSave, machine);
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private Machine createMachineSetAmusementPark() {
		Machine machine = createMachine();
		machine.setAmusementPark(amusementPark);
		return machine;
	}

	private void saveAll() {
		machineRepository.saveAll(Arrays.asList(createMachineSetAmusementPark(), createMachineSetAmusementPark()));
		insert += 2;
		incrementSelectIfOracleDBProfileActive(2);
		assertStatements();
	}

	private void sumAreaByAmusementParkId() {
		assertEquals(machine.getSize().intValue() * 3,
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
		assertTrue(machineRepository.findAllByAmusementParkId(amusementParkId).contains(machine));
		select++;
		assertStatements();
	}

	private void findById() {
		assertEquals(machine, machineRepository.findById(machine.getId()).get());
		select++;
		assertStatements();
	}

	private void findAll() {
		assertTrue(machineRepository.findAll().contains(machine));
		select++;
		assertStatements();
	}

	private void deleteById() {
		machineRepository.deleteById(machine.getId());
		select++;
		delete++;
		assertStatements();
	}

	private void deleteAll() {
		machineRepository.deleteAll();
		select++;
		delete += 2;
		assertStatements();
	}

}
