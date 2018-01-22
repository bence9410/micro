package hu.beni.amusementpark.test.integration;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.MachineService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static hu.beni.amusementpark.helper.ValidEntityFactory.*;
import static org.junit.Assert.*;

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
    public void test(){
        AmusementPark amusementPark = createAmusementParkWithAddress();
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
    
        amusementParkRepository.deleteById(amusementParkId);
    }

}
