package hu.beni.amusementpark.test.integration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorState;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.VisitorService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class VisitorServiceTests {
    
    @Autowired
    private AmusementParkService amusementParkService;
    
    @Autowired
    private MachineService machineService;
    
    @Autowired
    private VisitorService visitorService;
    
    @Test
    public void test(){
        AmusementPark amusementPark = AmusementPark.builder().capital(100).entranceFee(10).totalArea(100).address(Address.builder().build()).build();
        Long amusementParkId = amusementParkService.save(amusementPark).getId();
        Integer capital = amusementPark.getCapital();
        Integer entranceFee = amusementPark.getEntranceFee();
        
        Machine machine = Machine.builder().price(10).size(10).ticketPrice(10).minimumRequiredAge(20).numberOfSeats(10).build();
        Integer ticketPrice = machine.getTicketPrice();
        Long machineId = machineService.addMachine(amusementParkId, machine).getId();
        capital -= machine.getPrice();
        
        Visitor visitor = Visitor.builder().spendingMoney(40).age(25).build();
        Integer spendingMoney = visitor.getSpendingMoney();
        Long visitorId = visitorService.enterPark(amusementParkId, visitor).getId();
        capital += entranceFee;
        spendingMoney -= entranceFee;
        assertEquals(capital, amusementParkService.findOne(amusementParkId).getCapital());
        
        Visitor readVisitor = visitorService.findOne(visitorId);
        assertEquals(spendingMoney, readVisitor.getSpendingMoney());
        assertEquals(VisitorState.REST, readVisitor.getState());
        
        visitorService.getOnMachine(amusementParkId, machineId, visitorId);
        capital += ticketPrice;
        spendingMoney -= ticketPrice;
        assertEquals(capital, amusementParkService.findOne(amusementParkId).getCapital());
        
        readVisitor = visitorService.findOne(visitorId);
        assertEquals(spendingMoney, readVisitor.getSpendingMoney());
        assertEquals(VisitorState.ON_MACHINE, readVisitor.getState());
        
        visitorService.getOffMachine(machineId, visitorId);
        assertEquals(VisitorState.REST, visitorService.findOne(visitorId).getState());
        
        visitorService.leavePark(visitorId);
        assertNull(visitorService.findOne(visitorId));
    }

}
