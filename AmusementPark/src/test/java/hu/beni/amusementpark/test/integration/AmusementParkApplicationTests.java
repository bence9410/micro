package hu.beni.amusementpark.test.integration;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.*;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.helper.MyAssert.assertThrows;
import static hu.beni.amusementpark.helper.ValidEntityFactory.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AmusementParkApplicationTests {

	@Autowired
	private Environment environment;
	
    @Autowired
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;
    
    private String appUrl;
    
    private HttpHeaders httpHeaders;
    
    private String getAppUrl() {
        return appUrl == null ? appUrl = "http://localhost:" + port : appUrl;    	
    }
    
    @Before
    public void setUp() {
    	httpHeaders = new HttpHeaders();
    }

    @Test
    public void positiveTest() {
    	
    	loginAsAdminAndSetSessionIdInHeaders();
    	    	
        //create AmusementPark
        Resource<AmusementPark> amusementParkResource = postAmusementParkWithAddress();
        String amusementParkUrl = amusementParkResource.getId().getHref();
        
        assertEquals(amusementParkResource.getContent(), restTemplate.exchange(amusementParkUrl, HttpMethod.GET, createHttpEntityWithSessionIdHeaders(), amusementParkType()).getBody().getContent());
        
        //add Machine
        Resource<Machine> machineResource = postMachine(amusementParkResource.getLink(MACHINE).getHref());
        
        //visitor signUp
        Resource<Visitor> visitorResource = postVisitor(amusementParkResource.getLink(VISITOR_SIGN_UP).getHref());
        
        //visitor enterPark
        visitorResource = restTemplate.exchange(visitorResource.getLink(VISITOR_ENTER_PARK).getHref(), HttpMethod.PUT, createHttpEntityWithSessionIdHeaders(200), visitorType(), amusementParkResource.getContent().getId()).getBody();
        
        //visitor getOnMachine
        Link visitorGetOffMachineLink = restTemplate.exchange(machineResource.getLink(GET_ON_MACHINE).getHref(), HttpMethod.PUT, createHttpEntityWithSessionIdHeaders(), visitorType(), visitorResource.getContent().getId()).getBody().getLink(GET_OFF_MACHINE);
        
        //visitor getOffMachine
        restTemplate.exchange(visitorGetOffMachineLink.getHref(), HttpMethod.PUT, createHttpEntityWithSessionIdHeaders(), visitorType()).getBody();
        
        //addRegistry
        restTemplate.exchange(visitorResource.getLink(ADD_REGISTRY).getHref(), HttpMethod.POST, createHttpEntityWithSessionIdHeaders(OPINION_ON_THE_PARK), guestBookRegistryType(), visitorResource.getContent().getId()).getBody();
        
        //visitor leavePark
        restTemplate.exchange(visitorResource.getLink(VISITOR_LEAVE_PARK).getHref(), HttpMethod.PUT, createHttpEntityWithSessionIdHeaders(), Void.class).getStatusCode();

        //sell Machine
        restTemplate.exchange(machineResource.getId().getHref(), HttpMethod.DELETE, createHttpEntityWithSessionIdHeaders(), Void.class);
        
        //delete Park
        if (environment.getActiveProfiles().length == 0) {
        	assertThrows(() -> deleteAmusementPark(amusementParkUrl),
            		HttpClientErrorException.class, exception -> {
            		assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
            		assertEquals(NO_ARCHIVE_SEND_TYPE, exception.getResponseBodyAsString());
            	});
		} else {
			deleteAmusementPark(amusementParkUrl);
		}
		
        logout();
    }

    @Test
    public void negativeTest() {
    	loginAsAdminAndSetSessionIdInHeaders();
    	
    	assertThrows(() -> restTemplate.exchange(getAppUrl() + "/amusementPark", HttpMethod.POST, createHttpEntityWithSessionIdHeaders(createAmusementPark()), String.class),
    			HttpClientErrorException.class, exception -> {
    		assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
    		String errorMessage = exception.getResponseBodyAsString();
            assertTrue(errorMessage.contains("Validation error: "));
            assertTrue(errorMessage.contains("address"));
            assertTrue(errorMessage.contains("null"));
		});
        
        Resource<AmusementPark> amusementParkResource = postAmusementParkWithAddress();

        Machine machine = createMachine();
        machine.setPrice(4000);
                
        assertThrows(() -> restTemplate.exchange(amusementParkResource.getLink(MACHINE).getHref(), HttpMethod.POST, createHttpEntityWithSessionIdHeaders(machine), String.class),
        		HttpClientErrorException.class, exception -> {
            assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
            assertEquals(MACHINE_IS_TOO_EXPENSIVE, exception.getResponseBodyAsString());
        });
        
        logout();
    }
        
    private void loginAsAdminAndSetSessionIdInHeaders() {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    	MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
    	map.add("username", "admin");
    	map.add("password", "pass");
    	
    	ResponseEntity<String> response = restTemplate.exchange(getAppUrl() + "/login", HttpMethod.POST, 
    			new HttpEntity<MultiValueMap<String, String>>(map, headers), String.class);
    	
    	assertEquals(HttpStatus.FOUND, response.getStatusCode());
    	
    	HttpHeaders responseHeaders = response.getHeaders();
    	
    	assertTrue(responseHeaders.getLocation().toString().contains("home.html"));
    	    	
    	String cookie = response.getHeaders().getFirst("Set-Cookie");
    	
    	httpHeaders.add("Cookie", cookie.substring(0, cookie.indexOf(';')));
    }
    
    private void logout() {
    	ResponseEntity<String> response = restTemplate.exchange(getAppUrl() + "/logout", HttpMethod.POST,
    			createHttpEntityWithSessionIdHeaders(), String.class);
    	
    	assertEquals(HttpStatus.FOUND, response.getStatusCode());
    	assertTrue(response.getHeaders().getLocation().toString().contains("login?logout"));
    	
    	response = restTemplate.exchange(getAppUrl() + "/amusementPark", HttpMethod.POST, createHttpEntityWithSessionIdHeaders(), String.class);
        		
    	assertEquals(HttpStatus.FOUND, response.getStatusCode());
    	assertTrue(response.getHeaders().getLocation().toString().endsWith("login"));
    	
    	response = restTemplate.exchange(getAppUrl() + "/amusementPark", HttpMethod.GET, createHttpEntityWithSessionIdHeaders(), String.class);
    
    	assertEquals(HttpStatus.OK, response.getStatusCode());
    	assertTrue(response.getBody().length() > 450);
    }
    
    private <T> HttpEntity<T> createHttpEntityWithSessionIdHeaders(T body) {
    	return new HttpEntity<T>(body, httpHeaders);
    }
    
    private <T> HttpEntity<T> createHttpEntityWithSessionIdHeaders() {
    	return new HttpEntity<T>(httpHeaders);
    }
    
    private Resource<AmusementPark> postAmusementParkWithAddress() {
        AmusementPark amusementPark = createAmusementParkWithAddress();

        ResponseEntity<Resource<AmusementPark>> response = restTemplate.exchange(getAppUrl() + "/amusementPark", HttpMethod.POST, createHttpEntityWithSessionIdHeaders(amusementPark), amusementParkType());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Resource<AmusementPark> amusementParkResource = response.getBody();
        
        assertEquals(4, amusementParkResource.getLinks().size());
        assertTrue(amusementParkResource.getId().getHref().endsWith(amusementParkResource.getContent().getId().toString()));
        assertNotNull(amusementParkResource.getLink(MACHINE));
        assertNotNull(amusementParkResource.getLink(VISITOR_SIGN_UP));
        assertNotNull(amusementParkResource.getLink(VISITOR_ENTER_PARK));

        return amusementParkResource;
    }

    private Resource<Machine> postMachine(String url) {
        Machine machine = createMachine();

        Resource<Machine> machineResource = restTemplate.exchange(url, HttpMethod.POST, createHttpEntityWithSessionIdHeaders(machine), machineType()).getBody();

        assertEquals(2, machineResource.getLinks().size());
        assertNotNull(machineResource.getId());
        assertTrue(machineResource.getId().getHref().endsWith(machineResource.getContent().getId().toString()));
        assertNotNull(machineResource.getLink(GET_ON_MACHINE));

        return machineResource;
    }

    private Resource<Visitor> postVisitor(String url) {
        Visitor visitor = createVisitor();

        Resource<Visitor> visitorResource = restTemplate.exchange(url, HttpMethod.POST, createHttpEntityWithSessionIdHeaders(visitor), visitorType()).getBody();
        Long visitorId = visitorResource.getContent().getId();
        
        assertEquals(2, visitorResource.getLinks().size());
        assertTrue(visitorResource.getId().getHref().endsWith(visitorId.toString()));
        assertNotNull(visitorResource.getLink(VISITOR_ENTER_PARK));

        return visitorResource;
    }
    
    private void deleteAmusementPark(String amusementParkUrl) {
    	restTemplate.exchange(amusementParkUrl, HttpMethod.DELETE, createHttpEntityWithSessionIdHeaders(), Void.class);
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
    
    private ParameterizedTypeReference<Resource<GuestBookRegistry>> guestBookRegistryType(){
    	return new ParameterizedTypeReference<Resource<GuestBookRegistry>>() {
		};
    }

}
