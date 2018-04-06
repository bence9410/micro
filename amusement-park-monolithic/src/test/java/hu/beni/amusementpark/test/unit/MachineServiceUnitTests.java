package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.impl.MachineServiceImpl;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class MachineServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;
    private VisitorRepository visitorRepository;

    private MachineService machineService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        machineService = new MachineServiceImpl(amusementParkRepository, machineRepository, visitorRepository);
    }

    @After
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository);
    }

    @Test
    public void addMachineNegativeNoPark() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().build();

        assertThatThrownBy(() -> machineService.addMachine(amusementParkId, machine))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
    }

    @Test
    public void addMachineNegativeTooExpensiveMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).capital(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> machineService.addMachine(amusementParkId, machine))
        		.isInstanceOf(AmusementParkException.class).hasMessage(MACHINE_IS_TOO_EXPENSIVE);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
    }

    @Test
    public void addMachineNegativeTooBigMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).size(30).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.sumAreaByAmusementParkId(amusementParkId)).thenReturn(Optional.of(80L));

        assertThatThrownBy(() -> machineService.addMachine(amusementParkId, machine))
        		.isInstanceOf(AmusementParkException.class).hasMessage(MACHINE_IS_TOO_BIG);

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
        verify(machineRepository).sumAreaByAmusementParkId(amusementParkId);
    }

    @Test
    public void addMachinePositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).size(80).build();

        when(amusementParkRepository.findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.sumAreaByAmusementParkId(amusementParkId)).thenReturn(Optional.of(20L));
        when(machineRepository.save(machine)).thenReturn(machine);

        assertEquals(machine, machineService.addMachine(amusementPark.getId(), machine));

        assertEquals(amusementPark, machine.getAmusementPark());

        verify(amusementParkRepository).findByIdReadOnlyIdAndCapitalAndTotalArea(amusementParkId);
        verify(machineRepository).sumAreaByAmusementParkId(amusementParkId);
        verify(amusementParkRepository).decreaseCapitalById(machine.getPrice(), amusementParkId);
        verify(machineRepository).save(machine);
    }
    
    @Test
    public void findOneNegativeNoMachine() {
    	Long amusementParkId = 0L;
    	Long machineId = 1L;
    	
    	assertThatThrownBy(() -> machineService.findOne(amusementParkId, machineId))
    		.isInstanceOf(AmusementParkException.class)
    		.hasMessage(NO_MACHINE_IN_PARK_WITH_ID);
    	
    	verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);    	
    }
    
    @Test
    public void findOnePositive() {
    	Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(Optional.of(machine));

        assertEquals(machine, machineService.findOne(amusementParkId, machineId));

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }
    
    @Test
    public void findAllByAmusementParkIdPositive() {
    	Long amusementParkId = 0L;
    	List<Machine> machines = Arrays.asList(
    			Machine.builder().id(1L).build(),
    			Machine.builder().id(2L).build());
    	
    	when(machineRepository.findAllByAmusementParkId(amusementParkId)).thenReturn(machines);
    	
    	assertEquals(machines, machineService.findAllByAmusementParkId(amusementParkId));
    	
    	verify(machineRepository).findAllByAmusementParkId(amusementParkId);
    }

    @Test
    public void removeMachineNegativeNoMachine() {
        Long amusementParkId = 0L;
        Long machineId = 1L;

        assertThatThrownBy(() -> machineService.removeMachine(amusementParkId, machineId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }

    @Test
    public void removeMachineNegativeVisitorsOnMachine() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).price(150).build();
        Long machineId = machine.getId();
        Long numberOfVisitorsOnMachine = 10L;

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(Optional.of(machine));
        when(visitorRepository.countByMachineId(machineId)).thenReturn(numberOfVisitorsOnMachine);

        assertThatThrownBy(() -> machineService.removeMachine(amusementParkId, machineId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(VISITORS_ON_MACHINE);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).countByMachineId(machineId);
    }

    @Test
    public void removeMachinePositive() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).price(150).build();
        Long machineId = machine.getId();
        Long numberOfVisitorsOnMachine = 0L;

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(Optional.of(machine));
        when(visitorRepository.countByMachineId(machineId)).thenReturn(numberOfVisitorsOnMachine);

        machineService.removeMachine(amusementParkId, machineId);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).countByMachineId(machineId);
        verify(amusementParkRepository).incrementCapitalById(machine.getPrice(), amusementParkId);
        verify(machineRepository).deleteById(machineId);
    }
}
