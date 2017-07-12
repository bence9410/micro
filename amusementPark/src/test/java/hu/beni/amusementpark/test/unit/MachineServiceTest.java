package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.MachineService;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class MachineServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;
    private VisitorRepository visitorRepository;
    private MachineService machineService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        machineService = new MachineService(amusementParkRepository, machineRepository, visitorRepository);
    }

    @Test
    public void addMachineNegativeTooExpensiveMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).capital(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).build();

        when(amusementParkRepository.findOne(amusementParkId)).thenReturn(amusementPark);

        exception.expect(AmusementParkException.class);
        exception.expectMessage("Machine is too expensive!");

        machineService.addMachine(amusementParkId, machine);
    }

    @Test
    public void addMachineNegativeTooBigMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).size(30).build();

        when(amusementParkRepository.findOne(amusementParkId)).thenReturn(amusementPark);
        when(machineRepository.sumAreaByAmusementParkId(amusementParkId)).thenReturn(80L);

        exception.expect(AmusementParkException.class);
        exception.expectMessage("Machine is too big!");

        machineService.addMachine(amusementParkId, machine);
    }

    @Test
    public void addMachinePositive() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().price(200).size(30).build();

        when(amusementParkRepository.findOne(amusementParkId)).thenReturn(amusementPark);
        when(machineRepository.sumAreaByAmusementParkId(amusementParkId)).thenReturn(20L);

        machineService.addMachine(amusementPark.getId(), machine);
        
        verify(amusementParkRepository).findOne(amusementParkId);
        verify(machineRepository).sumAreaByAmusementParkId(amusementParkId);
        verify(amusementParkRepository).decreaseCapitalById(machine.getPrice(), amusementParkId);
        assertEquals(amusementPark, machine.getAmusementPark());
        verify(machineRepository).save(machine);
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository);
    }

    @Test
    public void removeMachineNegativeNoMachine() {
        Long amusementParkId = 0L;
        Long machineId = 1L;

        exception.expect(AmusementParkException.class);
        exception.expectMessage("No machine in the park with the given id!");

        machineService.removeMachine(amusementParkId, machineId);
    }

    @Test
    public void removeMachineNegativeVisitorsOnMachine() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).price(150).build();
        Long numberOfVisitorsOnMachine = 10L;

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machine.getId())).thenReturn(machine);
        when(visitorRepository.countByMachineId(machine.getId())).thenReturn(numberOfVisitorsOnMachine);

        exception.expect(AmusementParkException.class);
        exception.expectMessage("Visitors on machine!");

        machineService.removeMachine(amusementParkId, machine.getId());
    }
    
    @Test
    public void removeMachinePositive(){
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).price(150).build();
        Long machineId = machine.getId();
        Long numberOfVisitorsOnMachine = 0L;

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.countByMachineId(machineId)).thenReturn(numberOfVisitorsOnMachine);

        machineService.removeMachine(amusementParkId, machineId);
        
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).countByMachineId(machineId);
        verify(amusementParkRepository).incrementCapitalById(machine.getPrice(), amusementParkId);
        verify(machineRepository).delete(machineId);
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository);
    }
    
    @Test
    public void readPositive(){
        Machine machine = Machine.builder().id(0L).build();
        Long machineId = machine.getId();
        
        when(machineRepository.findOne(machineId)).thenReturn(machine);
        
        Machine readMachine = machineService.read(machineId);
        
        assertEquals(machine, readMachine);
        verify(machineRepository).findOne(machineId);
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository);
    }
}
