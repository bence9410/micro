package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class MachineServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private MachineService machineService;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	private AmusementPark amusementPark;
	private Long amusementParkId;

	private Machine machine;
	private List<Long> machineIds = new ArrayList<>();

	@Before
	public void setUp() {
		amusementPark = amusementParkRepository.save(createAmusementParkWithAddress());
		amusementParkId = amusementPark.getId();
		reset();
	}

	@Test
	public void test() {
		addMachine();
		addMachine();

		assertCapitalDecreased();

		findOne();

		findAllByAmusementParkId();

		machineIds.forEach(this::removeMachine);

		assertCapitalIncremented();
	}

	private void addMachine() {
		machine = machineService.addMachine(amusementParkId, createMachine());
		machineIds.add(machine.getId());
		select += 2;
		update++;
		insert++;
		incrementSelectIfOracleDBProfileActive();
		assertStatements();
	}

	private void assertCapitalDecreased() {
		assertEquals(amusementPark.getCapital() - machine.getPrice() * 2,
				amusementParkRepository.findById(amusementParkId).get().getCapital().longValue());
		select++;
		assertStatements();
	}

	private void findOne() {
		assertEquals(machine, machineService.findOne(amusementParkId, machine.getId()));
		select++;
		assertStatements();
	}

	private void findAllByAmusementParkId() {
		assertTrue(machineService.findAllByAmusementParkId(amusementParkId).contains(machine));
		select++;
		assertStatements();
	}

	private void removeMachine(Long machineId) {
		machineService.removeMachine(amusementParkId, machineId);
		select += 2;
		update++;
		delete++;
		assertStatements();
	}

	private void assertCapitalIncremented() {
		assertEquals(amusementPark.getCapital().longValue(),
				amusementParkRepository.findById(amusementParkId).get().getCapital().longValue());
		select++;
		assertStatements();
	}

}
