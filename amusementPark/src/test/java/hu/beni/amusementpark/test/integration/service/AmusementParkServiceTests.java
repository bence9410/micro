package hu.beni.amusementpark.test.integration.service;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.service.AmusementParkService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AmusementParkServiceTests {
    
    @Autowired
    private AmusementParkService amusementParkService;
    
    @Test
    public void test(){
        Address address = Address.builder().build();
        AmusementPark amusementPark = AmusementPark.builder().address(address).build();
        
        AmusementPark createdAmusementPark = amusementParkService.create(amusementPark);
        assertNotNull(createdAmusementPark);
        Long id = createdAmusementPark.getId();
        amusementPark.setId(id);
        address.setId(createdAmusementPark.getAddress().getId());
        
        assertNotNull(id);
        assertNotNull(address.getId());
        
        AmusementPark readAmusementPark = amusementParkService.read(id);
        assertEquals(amusementPark, readAmusementPark);
    
        amusementParkService.delete(id);
        assertNull(amusementParkService.read(id));    
    }

}
