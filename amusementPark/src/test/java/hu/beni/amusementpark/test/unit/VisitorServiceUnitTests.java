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
import hu.beni.amusementpark.service.impl.VisitorServiceImpl;

import org.junit.After;
import org.junit.Before;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import org.junit.Test;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;

public class VisitorServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;
    private VisitorRepository visitorRepository;

    private VisitorService visitorService;

    @Before
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        visitorService = new VisitorServiceImpl(amusementParkRepository, machineRepository, visitorRepository);
    }

    @After
    public void verifyNoMoreInteractionsOnMocks() {
    	verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository);
    }
    
    @Test
    public void signUpPositive() {
    	Visitor visitor = Visitor.builder().build();
    	
    	when(visitorRepository.save(visitor)).thenReturn(visitor);
    	
    	assertEquals(visitor, visitorService.signUp(visitor));
    	
    	verify(visitorRepository).save(visitor);
    }
    
    @Test
    public void findOnePositive() {
        Visitor visitor = Visitor.builder().id(0L).build();
        Long visitorId = visitor.getId();

        when(visitorRepository.findOne(visitorId)).thenReturn(visitor);

        assertEquals(visitor, visitorService.findOne(visitorId));

        verify(visitorRepository).findOne(visitorId);
    }
    
    @Test
    public void enterParkNegativeNoPark() {
        Long amusementParkId = 0L;
        Long visitorId = 1L;
        Integer entranceFee = 200;

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorId, entranceFee))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
    }
    
    @Test
    public void enterParkNegativeNotEnoughMoney() {
    	AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Long visitorId = 1L;
        Integer spendingMoney = 20;
        
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId)).thenReturn(amusementPark);
        
        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorId, spendingMoney))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
    }
    
    @Test
    public void enterParkNegativeVisitorIsInAPark() {
    	AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Long visitorId = 1L;
        Long numberOfVisitorsWithNotNullPark = 1L;
        Integer spendingMoney = 200;
        
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId)).thenReturn(amusementPark);
        when(visitorRepository.countByVisitorIdWhereAmusementParkIsNotNull(visitorId)).thenReturn(numberOfVisitorsWithNotNullPark);
        
        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorId, spendingMoney))
        		.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_IN_A_PARK);

        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(visitorRepository).countByVisitorIdWhereAmusementParkIsNotNull(visitorId);
    }   
    
    @Test
    public void enterParkNegativeVisitorNotRegistrated() {
    	AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Long visitorId = 1L;
        Long numberOfVisitorsWithNotNullPark = 0L;
        Integer spendingMoney = 200;
        
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId)).thenReturn(amusementPark);
        when(visitorRepository.countByVisitorIdWhereAmusementParkIsNotNull(visitorId)).thenReturn(numberOfVisitorsWithNotNullPark);
        
        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorId, spendingMoney))
        		.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_NOT_SIGNED_UP);

        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(visitorRepository).countByVisitorIdWhereAmusementParkIsNotNull(visitorId);
        verify(visitorRepository).findOne(visitorId);
    }
    
    @Test
    public void enterParkPositive() {
    	AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().id(1L).build();
        Long visitorId = visitor.getId();
        Long numberOfVisitorsWithNotNullPark = 0L;
        Integer spendingMoney = 200;
        
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId)).thenReturn(amusementPark);
        when(visitorRepository.countByVisitorIdWhereAmusementParkIsNotNull(visitorId)).thenReturn(numberOfVisitorsWithNotNullPark);
        when(visitorRepository.findOne(visitorId)).thenReturn(visitor);
        when(amusementParkRepository.countKnownVisitor(amusementParkId, visitorId)).thenReturn(0L);
        when(visitorRepository.save(visitor)).thenReturn(visitor);
        
        assertEquals(visitor, visitorService.enterPark(amusementParkId, visitorId, spendingMoney));

        assertEquals(spendingMoney - amusementPark.getEntranceFee(), visitor.getSpendingMoney().longValue());
        assertEquals(amusementPark, visitor.getAmusementPark());
        assertEquals(VisitorState.REST, visitor.getState());
        
        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(visitorRepository).countByVisitorIdWhereAmusementParkIsNotNull(visitorId);
        verify(amusementParkRepository).countKnownVisitor(amusementParkId, visitorId);
        verify(amusementParkRepository).addKnownVisitor(amusementParkId, visitorId);
        verify(visitorRepository).findOne(visitorId);
        verify(amusementParkRepository).incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
        verify(visitorRepository).save(visitor);
    }

    @Test
    public void getOnMachineNegativeNoMachineInPark() {
        Long amusementParkId = 0L;
        Long machineId = 1L;
        Long visitorId = 2L;

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }

    @Test
    public void getOnMachineNegativeNoVisitorInPark() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        Long visitorId = 2L;
        
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        
        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
    }

    @Test
    public void getOnMachineNegativeOnMachine() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).state(VisitorState.ON_MACHINE).build();
        Long visitorId = visitor.getId();

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_ON_A_MACHINE);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
    }
    
    @Test
    public void getOnMachineNegativeNotEnoughtMoney() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(50).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).build();
        Long visitorId = visitor.getId();

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
    }

    @Test
    public void getOnMachineNegativeTooYoung() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).dateOfBirth(Timestamp.from(Instant.now())).build();
        Long visitorId = visitor.getId();

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);
        
        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_TOO_YOUNG);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
    }
    
    @Test
    public void calculateAgeTest() {
    	try {
    		Method calculateAge = VisitorServiceImpl.class.getDeclaredMethod("calculateAge", Timestamp.class);
    		calculateAge.setAccessible(true);
    			
    		Calendar c = Calendar.getInstance();
    		c.set(Calendar.YEAR, c.get(Calendar.YEAR)-1);
    		
        	assertEquals(1, calculateAge.invoke(visitorService, Timestamp.from(c.toInstant())));
        	
        	c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR)-1);
        	
        	assertEquals(0, calculateAge.invoke(visitorService, Timestamp.from(c.toInstant())));
    	}catch (Exception e) {
    		e.printStackTrace();
    		fail("Could not find, access or call the calculacteAge method");
    	}
    }
    
    @Test
    public void getOnMachineNegativeNoFreeSeat() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).numberOfSeats(10).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).dateOfBirth(Timestamp.from(Instant.EPOCH)).build();
        Long visitorId = visitor.getId();

        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId)).thenReturn(machine);
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);
        when(visitorRepository.countByMachineId(machineId)).thenReturn(10L);

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NO_FREE_SEAT_ON_MACHINE);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
        verify(visitorRepository).countByMachineId(machineId);
    }

    
    @Test
    public void getOnMachinePositive() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).numberOfSeats(10).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().id(2L).spendingMoney(40).dateOfBirth(Timestamp.from(Instant.EPOCH)).build();
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
    public void getOffMachineNegativeNoVisitor() {
        Long machineId = 0L;
        Long visitorId = 1L;

        assertThatThrownBy(() -> visitorService.getOffMachine(machineId, visitorId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_ON_MACHINE_WITH_ID);

        verify(visitorRepository).findByMachineIdAndVisitorId(machineId, visitorId);
    }

    @Test
    public void getOffMachinePositive() {
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
    
    @Test
    public void leaveParkNegativeNoVisitorInPark() {
    	Long amusementParkId = 0L;
        Long visitorId = 1L;

        assertThatThrownBy(() -> visitorService.leavePark(amusementParkId, visitorId))
        		.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);
        
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);    
    }
    
    @Test
    public void leaveParkPositive() {
    	Long amusementParkId = 0L;
    	Visitor visitor = Visitor.builder().id(1L).build();
        Long visitorId = visitor.getId();
        
        when(visitorRepository.findByAmusementParkIdAndVisitorId(amusementParkId, visitorId)).thenReturn(visitor);
        
        visitorService.leavePark(amusementParkId, visitorId);
        
        verify(visitorRepository).findByAmusementParkIdAndVisitorId(amusementParkId, visitorId);
        verify(visitorRepository).save(visitor);
    }
   
}