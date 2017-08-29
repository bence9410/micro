package hu.beni.amusementpark.test.integration;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.enums.VisitorState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.constants.MappingConstants.*;
import static org.junit.Assert.*;
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
        return "http://localhost:" + port + "/amusementPark";
    }

    @Test
    public void positiveTest() {
        //create AmusementPark
        Resource<AmusementPark> amusementParkResource = postAmusementParkWithAddress();
        String amusementParkUrl = amusementParkResource.getId().getHref();
        AmusementPark amusementPark = amusementParkResource.getContent();

        //add Machine
        Resource<Machine> machineResource = postMachine(amusementParkResource.getLink(MACHINE).getHref());
        Machine machine = machineResource.getContent();
        amusementPark = checkIfAmusementParkCapitalReducedByMachinePrice(amusementParkUrl, amusementPark, machine.getPrice());

        //visitor enterPark
        Resource<Visitor> visitorResource = postVisitorAndCheckIfSpendingMoneyDecraisedByEntranceFee(amusementParkResource.getLink(VISITOR).getHref(), amusementPark.getEntranceFee());
        Visitor visitor = visitorResource.getContent();
        amusementPark = checkIfAmusementParkCapitalIncreasedByAmmount(amusementParkUrl, amusementPark, amusementPark.getEntranceFee());

        //visitor getOnMachine
        Resource<Visitor> visitorOnMachineResource = restTemplate.exchange(machineResource.getLink(GET_ON_MACHINE).getHref(), HttpMethod.PUT, HttpEntity.EMPTY, visitorType(), visitor.getId()).getBody();
        Visitor visitorOnMachine = visitorOnMachineResource.getContent();
        assertEquals(VisitorState.ON_MACHINE, visitorOnMachine.getState());
        assertEquals(visitor.getSpendingMoney() - machine.getTicketPrice(), visitorOnMachine.getSpendingMoney().longValue());
        amusementPark = checkIfAmusementParkCapitalIncreasedByAmmount(amusementParkUrl, amusementPark, machine.getTicketPrice());

        //visitor getOffMachine
        visitorResource = restTemplate.exchange(visitorOnMachineResource.getLink(GET_OFF_MACHINE).getHref(), HttpMethod.PUT, HttpEntity.EMPTY, visitorType()).getBody();
        assertEquals(VisitorState.REST, visitor.getState());

        //visitor leavePark
        restTemplate.exchange(visitorResource.getId().getHref(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        //sell Machine
        restTemplate.exchange(machineResource.getId().getHref(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
        checkIfAmusementParkCapitalIncreasedByAmmount(amusementParkUrl, amusementPark, machine.getPrice());

        //delete Park
        restTemplate.exchange(amusementParkUrl, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
    }

    @Test
    public void negativeTest() {
        ResponseEntity<String> errorResponse = restTemplate.exchange(getAmusementParkUrl(), HttpMethod.POST, new HttpEntity(createAmusementPark()), String.class);
        
        assertEquals(HttpStatus.I_AM_A_TEAPOT, errorResponse.getStatusCode());
        assertEquals("Validation error: address nem lehet null-érték.", errorResponse.getBody());
        
        Resource<AmusementPark> amusementParkResource = postAmusementParkWithAddress();

        Machine machine = createMachine();
        machine.setPrice(4000);

        errorResponse = restTemplate.exchange(amusementParkResource.getLink(MACHINE).getHref(), HttpMethod.POST, new HttpEntity(machine), String.class);

        assertEquals(HttpStatus.I_AM_A_TEAPOT, errorResponse.getStatusCode());
        assertEquals(MACHINE_IS_TOO_EXPENSIVE, errorResponse.getBody());

        machine.setPrice(400);
        machine.setSize(1100);

        errorResponse = restTemplate.exchange(amusementParkResource.getLink(MACHINE).getHref(), HttpMethod.POST, new HttpEntity(machine), String.class);

        assertEquals(HttpStatus.I_AM_A_TEAPOT, errorResponse.getStatusCode());
        assertEquals(MACHINE_IS_TOO_BIG, errorResponse.getBody());

    }

    private Resource<AmusementPark> postAmusementParkWithAddress() {
        AmusementPark amusementPark = createAmusementPark();
        amusementPark.setAddress(createAddress());

        Resource<AmusementPark> amusementParkResource = restTemplate.exchange(getAmusementParkUrl(), HttpMethod.POST, new HttpEntity<>(amusementPark), amusementParkType()).getBody();

        AmusementPark responseAmusementPark = amusementParkResource.getContent();
        assertNotNull(responseAmusementPark);
        assertNotNull(responseAmusementPark.getId());
        assertNotNull(responseAmusementPark.getAddress().getId());

        amusementPark.setId(responseAmusementPark.getId());
        amusementPark.getAddress().setId(responseAmusementPark.getAddress().getId());
        assertEquals(amusementPark, responseAmusementPark);

        assertEquals(3, amusementParkResource.getLinks().size());
        assertNotNull(amusementParkResource.getId().getHref());
        assertNotNull(responseAmusementPark.getId());
        assertTrue(amusementParkResource.getId().getHref().endsWith(responseAmusementPark.getId().toString()));
        assertNotNull(amusementParkResource.getLink(MACHINE));
        assertNotNull(amusementParkResource.getLink(VISITOR));

        return amusementParkResource;
    }

    private Resource<Machine> postMachine(String url) {
        Machine machine = createMachine();

        Resource<Machine> machineResource = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(machine), machineType()).getBody();

        Machine responseMachine = machineResource.getContent();
        assertNotNull(responseMachine);
        assertNotNull(responseMachine.getId());

        machine.setId(responseMachine.getId());
        assertEquals(machine, responseMachine);

        assertEquals(2, machineResource.getLinks().size());
        assertTrue(machineResource.getId().getHref().endsWith(responseMachine.getId().toString()));
        assertNotNull(machineResource.getLink(GET_ON_MACHINE));

        return machineResource;
    }

    private Resource<Visitor> postVisitorAndCheckIfSpendingMoneyDecraisedByEntranceFee(String url, Integer entranceFee) {
        Visitor visitor = createVisitor();

        Resource<Visitor> visitorResource = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(visitor), visitorType()).getBody();

        Visitor responseVisitor = visitorResource.getContent();
        assertNotNull(responseVisitor);
        assertNotNull(responseVisitor.getId());

        visitor.setId(responseVisitor.getId());
        assertEquals(visitor.getSpendingMoney().longValue(), responseVisitor.getSpendingMoney() + entranceFee);

        assertEquals(1, visitorResource.getLinks().size());
        assertTrue(visitorResource.getId().getHref().endsWith(responseVisitor.getId().toString()));

        return visitorResource;
    }

    private AmusementPark checkIfAmusementParkCapitalReducedByMachinePrice(String amusementParkUrl, AmusementPark amusementPark, Integer machinePrice) {
        Resource<AmusementPark> freshAmusementParkResource = restTemplate.exchange(amusementParkUrl, HttpMethod.GET, HttpEntity.EMPTY, amusementParkType()).getBody();
        AmusementPark freshAmusementPark = freshAmusementParkResource.getContent();

        assertNotNull(freshAmusementPark);
        assertEquals(amusementPark.getCapital() - machinePrice, freshAmusementPark.getCapital().longValue());

        return freshAmusementPark;
    }

    private AmusementPark checkIfAmusementParkCapitalIncreasedByAmmount(String amusementParkUrl, AmusementPark amusementPark, Integer ammount) {
        Resource<AmusementPark> freshAmusementParkResource = restTemplate.exchange(amusementParkUrl, HttpMethod.GET, HttpEntity.EMPTY, amusementParkType()).getBody();
        AmusementPark freshAmusementPark = freshAmusementParkResource.getContent();

        assertNotNull(freshAmusementPark);
        assertEquals(amusementPark.getCapital() + ammount, freshAmusementPark.getCapital().longValue());

        return freshAmusementPark;
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

}
