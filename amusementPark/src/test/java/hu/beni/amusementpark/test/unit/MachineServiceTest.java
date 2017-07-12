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
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(100).build();
        Machine machine = Machine.builder().price(200).build();

        when(amusementParkRepository.findOne(amusementPark.getId())).thenReturn(amusementPark);

        exception.expect(AmusementParkException.class);
        exception.expectMessage("Machine is too expensive!");

        machineService.addMachine(amusementPark.getId(), machine);
    }

    @Test
    public void addMachineNegativeTooBigMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(10L).capital(300).totalArea(100).build();
        Machine machine = Machine.builder().price(200).size(30).build();

        when(amusementParkRepository.findOne(amusementPark.getId())).thenReturn(amusementPark);
        when(machineRepository.sumAreaByAmusementParkId(amusementPark.getId())).thenReturn(80L);

        exception.expect(AmusementParkException.class);
        exception.expectMessage("Machine is too big!");

        machineService.addMachine(amusementPark.getId(), machine);
    }

}
