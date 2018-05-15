package hu.beni.amusementpark.test.integration;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.MachineService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static hu.beni.amusementpark.helper.ValidEntityFactory.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MachineServiceIntegrationTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private MachineService machineService;

	@Autowired
	private AmusementParkRepository amusementParkRepository;

	@Test
	public void test() {
		AmusementPark amusementPark = createAmusementParkWithAddress();
		Long amusementParkId = amusementParkService.save(amusementPark).getId();

		Machine machine = createMachine();
		Long machineId = machineService.addMachine(amusementParkId, machine).getId();
		assertNotNull(machineId);
		assertEquals(amusementPark.getCapital() - machine.getPrice(),
				amusementParkService.findByIdFetchAddress(amusementParkId).getCapital().longValue());

		Machine readMachine = machineService.findOne(amusementParkId, machineId);
		assertEquals(machine, readMachine);

		machineService.removeMachine(amusementParkId, machineId);
		assertThatThrownBy(() -> machineService.findOne(amusementParkId, machineId))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);
		assertEquals(amusementPark.getCapital().longValue(),
				amusementParkService.findByIdFetchAddress(amusementParkId).getCapital().longValue());

		amusementParkRepository.deleteById(amusementParkId);
	}

}
