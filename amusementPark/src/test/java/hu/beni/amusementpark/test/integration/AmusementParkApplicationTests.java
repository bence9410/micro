package hu.beni.amusementpark.test.integration;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBook;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.MachineType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.*;
import static org.junit.Assert.*;
import static hu.beni.amusementpark.test.TestConstants.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AmusementParkApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getAmusementParkUrl() {
        return LOCALHOST + port + SLASH_AMUSEMENT_PARK;
    }

    @Test
    public void positiveTest() {
        //create AmusementPark
        Resource<AmusementPark> amusementParkResource = postAmusementParkWithAddress();
        String amusementParkUrl = amusementParkResource.getId().getHref();
        
        //add Machine
        Resource<Machine> machineResource = postMachine(amusementParkResource.getLink(MACHINE).getHref());
        
        //visitor enterPark
        Resource<Visitor> visitorResource = postVisitor(amusementParkResource.getLink(VISITOR).getHref());
        
        //visitor getOnMachine
        Link visitorGetOffMachineLink = restTemplate.exchange(machineResource.getLink(GET_ON_MACHINE).getHref(), HttpMethod.PUT, HttpEntity.EMPTY, visitorType(), visitorResource.getContent().getId()).getBody().getLink(GET_OFF_MACHINE);
        
        //visitor getOffMachine
        restTemplate.exchange(visitorGetOffMachineLink.getHref(), HttpMethod.PUT, HttpEntity.EMPTY, visitorType()).getBody();
        
        //writeInGuestBook
        restTemplate.exchange(visitorResource.getLink(WRITE_IN_GUEST_BOOK).getHref(), HttpMethod.POST, new HttpEntity<>(OPINION_ON_THE_PARK), guestBookType()).getBody();
        
        //visitor leavePark
        restTemplate.exchange(visitorResource.getId().getHref(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        //sell Machine
        restTemplate.exchange(machineResource.getId().getHref(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        
        //delete Park
        restTemplate.exchange(amusementParkUrl, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
    }

    @Test
    public void negativeTest() {
        ResponseEntity<String> errorResponse = restTemplate.exchange(getAmusementParkUrl(), HttpMethod.POST, new HttpEntity<>(createAmusementPark()), String.class);
        
        assertEquals(HttpStatus.I_AM_A_TEAPOT, errorResponse.getStatusCode());
        String errorMessage = errorResponse.getBody();
        assertTrue(errorMessage.contains("Validation error: "));
        assertTrue(errorMessage.contains("address"));
        assertTrue(errorMessage.contains("null"));
        
        Resource<AmusementPark> amusementParkResource = postAmusementParkWithAddress();

        Machine machine = createMachine();
        machine.setPrice(4000);

        errorResponse = restTemplate.exchange(amusementParkResource.getLink(MACHINE).getHref(), HttpMethod.POST, new HttpEntity<>(machine), String.class);

        assertEquals(HttpStatus.I_AM_A_TEAPOT, errorResponse.getStatusCode());
        assertEquals(MACHINE_IS_TOO_EXPENSIVE, errorResponse.getBody());
    }

    private Resource<AmusementPark> postAmusementParkWithAddress() {
        AmusementPark amusementPark = createAmusementPark();
        amusementPark.setAddress(createAddress());

        Resource<AmusementPark> amusementParkResource = restTemplate.exchange(getAmusementParkUrl(), HttpMethod.POST, new HttpEntity<>(amusementPark), amusementParkType()).getBody();
        
        assertEquals(NUMBER_OF_LINKS_IN_AMUSEMENT_PARK_RESOURCE, amusementParkResource.getLinks().size());
        assertTrue(amusementParkResource.getId().getHref().endsWith(amusementParkResource.getContent().getId().toString()));
        assertNotNull(amusementParkResource.getLink(MACHINE));
        assertNotNull(amusementParkResource.getLink(VISITOR));

        return amusementParkResource;
    }

    private Resource<Machine> postMachine(String url) {
        Machine machine = createMachine();

        Resource<Machine> machineResource = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(machine), machineType()).getBody();

        assertEquals(NUMBER_OF_LINKS_IN_MACHINE_RESOURCE, machineResource.getLinks().size());
        assertTrue(machineResource.getId().getHref().endsWith(machineResource.getContent().getId().toString()));
        assertNotNull(machineResource.getLink(GET_ON_MACHINE));

        return machineResource;
    }

    private Resource<Visitor> postVisitor(String url) {
        Visitor visitor = createVisitor();

        Resource<Visitor> visitorResource = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(visitor), visitorType()).getBody();
        Long visitorId = visitorResource.getContent().getId();
        
        assertEquals(NUMBER_OF_LINKS_IN_VISITOR_RESOURCE_AFTER_ENTER, visitorResource.getLinks().size());
        assertTrue(visitorResource.getId().getHref().endsWith(visitorId.toString()));
        assertTrue(visitorResource.getLink(WRITE_IN_GUEST_BOOK).getHref().endsWith(visitorId + "/guestBook"));

        return visitorResource;
    }

    private AmusementPark createAmusementPark() {
        return AmusementPark.builder()
                .name("Park")
                .capital(3000)
                .totalArea(1000)
                .entranceFee(50).build();
    }

    private Address createAddress() {
        return Address.builder()
                .zipCode(1148)
                .city("Budapest")
                .country("Magyarország")
                .street("Fogarasi út")
                .houseNumber("80/C").build();
    }

    private Machine createMachine() {
        return Machine.builder()
                .fantasyName("Nagy hajó")
                .size(100)
                .price(250)
                .numberOfSeats(10)
                .minimumRequiredAge(18)
                .ticketPrice(10)
                .type(MachineType.CAROUSEL).build();
    }

    private Visitor createVisitor() {
        return Visitor.builder()
                .spendingMoney(100)
                .age(20).build();
    }

    private ParameterizedTypeReference<Resource<AmusementPark>> amusementParkType() {
        return new ParameterizedTypeReference<Resource<AmusementPark>>() {
        };
    }

    private ParameterizedTypeReference<Resource<Machine>> machineType() {
        return new ParameterizedTypeReference<Resource<Machine>>() {
        };
    }

    private ParameterizedTypeReference<Resource<Visitor>> visitorType() {
        return new ParameterizedTypeReference<Resource<Visitor>>() {
        };
    }
    
    private ParameterizedTypeReference<Resource<GuestBook>> guestBookType(){
    	return new ParameterizedTypeReference<Resource<GuestBook>>() {
		};
    }

}
