package hu.beni.amusementpark.test.integration;

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
import static hu.beni.amusementpark.test.ValidEntityFactory.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MachineServiceTests {
    
    @Autowired
    private AmusementParkService amusementParkService;
    
    @Autowired
    private MachineService machineService;
    
    @Test
    public void test(){
        AmusementPark amusementPark = createAmusementPark();
        amusementPark.setAddress(createAddress());
        Long amusementParkId = amusementParkService.save(amusementPark).getId();
        
        Machine machine = createMachine();
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
