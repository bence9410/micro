package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import org.junit.After;
import org.junit.Before;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import org.junit.Test;
import static hu.beni.amusementpark.test.MyAssert.assertThrows;

public class VisitorServiceTest {
    
    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;
    private VisitorRepository visitorRepository;

    private VisitorService visitorService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        visitorService = new VisitorService(amusementParkRepository, machineRepository, visitorRepository);
    }
    
    @After
    public void verifyNoMoreInteractionsOnMocks(){
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository);
    }
    
    @Test
    public void readPositive(){
        Visitor visitor = Visitor.builder().id(0L).build();
        Long visitorId = visitor.getId();
        
        when(visitorRepository.findOne(visitorId)).thenReturn(visitor);
        
        assertEquals(visitor, visitorService.read(visitorId));
        
        verify(visitorRepository).findOne(visitorId);
    }
    
    @Test
    public void leaveParkPositive(){
        Long visitorId = 0L;
        
        visitorService.leavePark(visitorId);
        
        verify(visitorRepository).delete(visitorId);        
    }
    
    @Test
    public void enterParkNegativeNoPark(){
        Long amusementParkId = 0L;
        Visitor visitor = Visitor.builder().build();
        
        assertThrows(() -> visitorService.enterPark(amusementParkId, visitor), AmusementParkException.class, "No AmusementPark with the given id!");
        
        verify(amusementParkRepository).findAmusementParkByIdReadOnlyIdAndEntranceFee(amusementParkId);
    }
    
    @Test
    public void enterParkNegativeNotEnoughMoney(){
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().spendingMoney(40).build();
        
        when(amusementParkRepository.findAmusementParkByIdReadOnlyIdAndEntranceFee(amusementParkId)).thenReturn(amusementPark);
        
        assertThrows(() -> visitorService.enterPark(amusementParkId, visitor), AmusementParkException.class, "Not enough money!");
        
        verify(amusementParkRepository).findAmusementParkByIdReadOnlyIdAndEntranceFee(amusementParkId);
    }
    
    @Test
    public void enterParkPositive(){
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Integer entranceFee = amusementPark.getEntranceFee();
        Visitor visitor = Visitor.builder().spendingMoney(100).build();
        Integer spendingMoney = visitor.getSpendingMoney();
        
        when(amusementParkRepository.findAmusementParkByIdReadOnlyIdAndEntranceFee(amusementParkId)).thenReturn(amusementPark);
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        
        assertEquals(visitor, visitorService.enterPark(amusementParkId, visitor));
        
        assertEquals(spendingMoney - entranceFee, visitor.getSpendingMoney().longValue());
        assertEquals(amusementPark, visitor.getAmusementPark());
        
        verify(amusementParkRepository).findAmusementParkByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(amusementParkRepository).incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
        verify(visitorRepository).save(visitor);
    }
    
    @Test
    public void getOnMachineNegativeNoMachine(){
        Long amusementParkId = 0L;
        Long machineId = 1L;
        Long visitorId = 2L;
        
        assertThrows(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId), AmusementParkException.class, "No machine in the park with the given id!");
        
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }
    
    @Test
    public void getOnMachineNegativeNoVisitor(){
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        Long visitorId = 2L;
        
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        
        assertThrows(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId), AmusementParkException.class, "No visitor in the park with the given id!");
        
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);        
    }
    
    @Test
    public void getOnMachineNegativeOnMachine(){
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).state(VisitorState.ON_MACHINE).build();
        Long visitorId = visitor.getId();
        
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);
        
        assertThrows(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId), AmusementParkException.class, "Visitor is on a machine");
        
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);        
    }
    
    @Test
    public void getOnMachineNegativeNotEnoughtMoney(){
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(50).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).build();
        Long visitorId = visitor.getId();
        
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);
        
        assertThrows(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId), AmusementParkException.class, "Not enough money!");
        
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);        
    }
    
    @Test
    public void getOnMachineNegativeTooYoung(){
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).numberOfSeats(10).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).age(25).build();
        Long visitorId = visitor.getId();
        
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);
        when(visitorRepository.countByMachineId(machineId)).thenReturn(10L);
        
        assertThrows(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId), AmusementParkException.class, "No free seat on machine");
        
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);  
        verify(visitorRepository).countByMachineId(machineId);
    }
    
    @Test
    public void getOnMachinePositive(){
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).numberOfSeats(10).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).age(25).build();
        Long visitorId = visitor.getId();
        Integer spendingMoney = visitor.getSpendingMoney();
        
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);
        when(visitorRepository.countByMachineId(machineId)).thenReturn(1L);
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        
        assertEquals(visitor, visitorService.getOnMachine(amusementParkId, machineId, visitorId));
        
        assertEquals(spendingMoney - machine.getTicketPrice(), visitor.getSpendingMoney().longValue());
        assertEquals(machine, visitor.getMachine());
        assertEquals(VisitorState.ON_MACHINE, visitor.getState());
        
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);  
        verify(visitorRepository).countByMachineId(machineId);
        verify(amusementParkRepository).incrementCapitalById(machine.getTicketPrice(), amusementParkId);
        verify(visitorRepository).save(visitor);
    }
    
    @Test
    public void getOffMachineNegativeNoVisitor(){
        Long machineId = 0L;
        Long visitorId = 1L;
        
        assertThrows(() -> visitorService.getOffMachine(machineId, visitorId), AmusementParkException.class, "No visitor on machine with the given id!");
        
        verify(visitorRepository).findByMachineIdAndVisitorId(machineId, visitorId);
    }
    
    @Test
    public void getOffMachinePositive(){
        Long machineId = 0L;
        Visitor visitor = Visitor.builder().id(1L).state(VisitorState.ON_MACHINE).machine(Machine.builder().build()).build();
        Long visitorId = visitor.getId();
        
        when(visitorRepository.findByMachineIdAndVisitorId(machineId, visitorId)).thenReturn(visitor);
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        
        assertEquals(visitor, visitorService.getOffMachine(machineId, visitorId));
        
        assertNull(visitor.getMachine());
        assertEquals(VisitorState.REST, visitor.getState());
        
        verify(visitorRepository).findByMachineIdAndVisitorId(machineId, visitorId);
        verify(visitorRepository).save(visitor);
    }
    

}
