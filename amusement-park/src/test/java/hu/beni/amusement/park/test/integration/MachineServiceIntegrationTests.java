package hu.beni.amusement.park.test.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusement.park.entity.AmusementPark;
import hu.beni.amusement.park.entity.Machine;
import hu.beni.amusement.park.repository.AmusementParkRepository;
import hu.beni.amusement.park.service.AmusementParkService;
import hu.beni.amusement.park.service.MachineService;

import static hu.beni.amusement.park.helper.ValidEntityFactory.*;
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
    
        amusementParkRepository.delete(amusementParkId);
    }

}
