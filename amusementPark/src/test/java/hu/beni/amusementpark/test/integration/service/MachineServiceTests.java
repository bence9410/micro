package hu.beni.amusementpark.test.integration.service;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.MachineService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MachineServiceTests {
    
    @Autowired
    private AmusementParkService amusementParkService;
    
    @Autowired
    private MachineService machineService;
    
    @Test
    public void test(){
        AmusementPark amusementPark = AmusementPark.builder().capital(100).totalArea(100).address(Address.builder().build()).build();
        Long amusementParkId = amusementParkService.save(amusementPark).getId();
        
        Machine machine = Machine.builder().price(10).size(10).build();
        Long machineId = machineService.addMachine(amusementParkId, machine).getId();
        assertNotNull(machineId);
        assertEquals(amusementPark.getCapital() - machine.getPrice(), amusementParkService.findOne(amusementParkId).getCapital().longValue());
        
        Machine readMachine = machineService.findOne(machineId);
        assertEquals(machine, readMachine);
        
        machineService.removeMachine(amusementParkId, machineId);
        assertNull(machineService.findOne(machineId));
        assertEquals(amusementPark.getCapital().longValue(), amusementParkService.findOne(amusementParkId).getCapital().longValue());
    }

}
