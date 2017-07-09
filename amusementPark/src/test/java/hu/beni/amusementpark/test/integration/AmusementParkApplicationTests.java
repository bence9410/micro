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

import static org.junit.Assert.*;

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
    public void test() {
        Resource<AmusementPark> amusementParkResource = postAmusementParkWithAddress();
        String amusementParkUrl = amusementParkResource.getId().getHref();

        Resource<Machine> machineResource = postMachine(amusementParkResource.getLink("machine").getHref());

        amusementParkResource = checkIfAmusementParkCapitalReducedByMachinePrice(amusementParkUrl, amusementParkResource.getContent(), machineResource.getContent().getPrice());

        Resource<Visitor> visitor = postVisitorAndCheckIfSpendingMoneyDecraisedByEntranceFee(amusementParkResource.getLink("visitor").getHref(), amusementParkResource.getContent().getEntranceFee());

        amusementParkResource = checkIfAmusementParkCapitalIncreasedByAmmount(amusementParkUrl, amusementParkResource.getContent(), amusementParkResource.getContent().getEntranceFee());

        Resource<Visitor> visitorOnMachine = restTemplate.exchange(machineResource.getLink("getOnMachine").getHref(), HttpMethod.PUT, HttpEntity.EMPTY, visitorType(), visitor.getContent().getId()).getBody();
        assertEquals(VisitorState.ON_MACHINE, visitorOnMachine.getContent().getState());
        assertEquals(visitor.getContent().getSpendingMoney() - machineResource.getContent().getTicketPrice(), visitorOnMachine.getContent().getSpendingMoney().longValue());

        amusementParkResource = checkIfAmusementParkCapitalIncreasedByAmmount(amusementParkUrl, amusementParkResource.getContent(), machineResource.getContent().getTicketPrice());

        visitor = restTemplate.exchange(visitorOnMachine.getLink("getOffMachine").getHref(), HttpMethod.PUT, HttpEntity.EMPTY, visitorType()).getBody();
        assertEquals(VisitorState.REST, visitor.getContent().getState());

        restTemplate.exchange(visitor.getId().getHref(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        restTemplate.exchange(machineResource.getId().getHref(), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        checkIfAmusementParkCapitalIncreasedByAmmount(amusementParkUrl, amusementParkResource.getContent(), machineResource.getContent().getPrice());

        restTemplate.exchange(amusementParkUrl, HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
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
        assertNotNull(amusementParkResource.getLink("machine"));
        assertNotNull(amusementParkResource.getLink("visitor"));

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
        assertNotNull(machineResource.getLink("getOnMachine"));

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

    private Resource<AmusementPark> checkIfAmusementParkCapitalReducedByMachinePrice(String amusementParkUrl, AmusementPark amusementPark, Integer machinePrice) {
        Resource<AmusementPark> freshAmusementParkResource = restTemplate.exchange(amusementParkUrl, HttpMethod.GET, HttpEntity.EMPTY, amusementParkType()).getBody();
        AmusementPark freshAmusementPark = freshAmusementParkResource.getContent();

        assertNotNull(freshAmusementPark);
        assertEquals(amusementPark.getCapital() - machinePrice, freshAmusementPark.getCapital().longValue());

        return freshAmusementParkResource;
    }

    private Resource<AmusementPark> checkIfAmusementParkCapitalIncreasedByAmmount(String amusementParkUrl, AmusementPark amusementPark, Integer ammount) {
        Resource<AmusementPark> freshAmusementParkResource = restTemplate.exchange(amusementParkUrl, HttpMethod.GET, HttpEntity.EMPTY, amusementParkType()).getBody();
        AmusementPark freshAmusementPark = freshAmusementParkResource.getContent();

        assertNotNull(freshAmusementPark);
        assertEquals(amusementPark.getCapital() + ammount, freshAmusementPark.getCapital().longValue());

        return freshAmusementParkResource;
    }

    private AmusementPark createAmusementPark() {
        return AmusementPark.builder()
                .name("Park")
                .capital(30000)
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
