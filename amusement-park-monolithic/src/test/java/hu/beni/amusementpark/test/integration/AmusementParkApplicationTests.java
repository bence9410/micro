package hu.beni.amusementpark.test.integration;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.Client;
import hu.beni.amusementpark.helper.MyAssert.ExceptionAsserter;
import hu.beni.amusementpark.helper.ValidEntityFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.constants.HATEOASLinkNameConstants.*;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.helper.MyAssert.assertThrows;
import static hu.beni.amusementpark.helper.ValidEntityFactory.*;
import static org.junit.Assert.*;

import java.net.URI;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AmusementParkApplicationTests {

	private static final ParameterizedTypeReference<Resource<AmusementPark>> AMUSEMENT_PARK_TYPE = new ParameterizedTypeReference<Resource<AmusementPark>>() {
	};

	private static final ParameterizedTypeReference<Resource<Machine>> MACHINE_TYPE = new ParameterizedTypeReference<Resource<Machine>>() {
	};

	private static final ParameterizedTypeReference<Resource<Visitor>> VISITOR_TYPE = new ParameterizedTypeReference<Resource<Visitor>>() {
	};

	private static final ParameterizedTypeReference<Resource<GuestBookRegistry>> GUEST_BOOK_REGISTRY_TYPE = new ParameterizedTypeReference<Resource<GuestBookRegistry>>() {
	};

	@Autowired
	private Environment environment;

	@Autowired
	private Client client;

	@LocalServerPort
	private int port;

	private String amusementParkUrl;

	private String logoutUrl;

	@PostConstruct
	public void init() {
		String baseUrl = "http://localhost:" + port;
		amusementParkUrl = baseUrl + "/amusement-park";
		logoutUrl = baseUrl + "/logout";
	}

	@Test
	public void positiveTest() {
		login("admin", "pass");

		Resource<AmusementPark> amusementParkResource = createAmusementPark();

		Resource<Machine> machineResource = addMachine(amusementParkResource.getLink(MACHINE).getHref());

		Resource<Visitor> visitorResource = signUp(amusementParkResource.getLink(VISITOR_SIGN_UP).getHref());

		visitorResource = enterPark(visitorResource.getLink(VISITOR_ENTER_PARK).getHref(),
				amusementParkResource.getContent().getId());

		visitorResource = getOnMachine(machineResource.getLink(GET_ON_MACHINE).getHref(),
				visitorResource.getContent().getId());

		visitorResource = getOffMachine(visitorResource.getLink(GET_OFF_MACHINE).getHref());

		addRegistry(visitorResource.getLink(ADD_REGISTRY).getHref());

		leavePark(visitorResource.getLink(VISITOR_LEAVE_PARK).getHref());

		sellMachine(machineResource.getId().getHref());

		deletePark(amusementParkResource.getId().getHref());

		logout();
	}

	@Test
	public void negativeTest() {
		login("admin", "pass");

		assertThrows(() -> client.post(uri(amusementParkUrl), ValidEntityFactory.createAmusementPark(), String.class),
				HttpClientErrorException.class, teaPotStatusAndAddressNullMessage());

		Resource<AmusementPark> amusementParkResource = createAmusementPark();

		Machine machine = createMachine();
		machine.setPrice(4000);

		assertThrows(() -> client.post(uri(amusementParkResource.getLink(MACHINE).getHref()), machine, String.class),
				HttpClientErrorException.class, teaPotStatusAndMachineTooExpensiveMessage());

		logout();
	}

	private void login(String username, String password) {
		ResponseEntity<Void> response = client.post(uri(getLoginUrl()), MediaType.APPLICATION_FORM_URLENCODED,
				createMap(username, password), Void.class);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());

		HttpHeaders responseHeaders = response.getHeaders();

		assertTrue(responseHeaders.getLocation().toString().contains("home.html"));
		assertTrue(responseHeaders.getFirst("Set-Cookie").contains("JSESSIONID="));
	}

	private MultiValueMap<String, String> createMap(String username, String password) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", username);
		map.add("password", password);
		return map;
	}

	private String getLoginUrl() {
		ResponseEntity<Void> response = client.post(uri(amusementParkUrl), null, Void.class);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());

		URI location = response.getHeaders().getLocation();
		assertNotNull(location);

		String locationAsString = location.toString();
		assertTrue(locationAsString.endsWith("login"));

		return locationAsString;
	}

	private void logout() {
		ResponseEntity<Void> response = client.post(uri(logoutUrl), null, Void.class);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(response.getHeaders().getLocation().toString().contains("login?logout"));

		testRedirectToLoginPage();

		restTemplateFollowsRedirectOnGet();
	}

	private void testRedirectToLoginPage() {
		ResponseEntity<Void> response = client.post(uri(amusementParkUrl), null, Void.class);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(response.getHeaders().getLocation().toString().endsWith("login"));
	}

	private void restTemplateFollowsRedirectOnGet() {
		ResponseEntity<String> loginPageResponse = client.get(uri(amusementParkUrl), String.class);

		assertEquals(HttpStatus.OK, loginPageResponse.getStatusCode());
		assertTrue(loginPageResponse.getBody().length() > 450);
	}

	private Resource<AmusementPark> createAmusementPark() {
		AmusementPark amusementPark = createAmusementParkWithAddress();

		ResponseEntity<Resource<AmusementPark>> response = client.post(uri(amusementParkUrl), amusementPark,
				AMUSEMENT_PARK_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Resource<AmusementPark> amusementParkResource = response.getBody();

		assertNotNull(amusementParkResource);
		assertEquals(4, amusementParkResource.getLinks().size());
		assertTrue(amusementParkResource.getId().getHref()
				.endsWith(amusementParkResource.getContent().getId().toString()));
		assertNotNull(amusementParkResource.getLink(MACHINE));
		assertNotNull(amusementParkResource.getLink(VISITOR_SIGN_UP));
		assertNotNull(amusementParkResource.getLink(VISITOR_ENTER_PARK));

		return amusementParkResource;
	}

	private Resource<Machine> addMachine(String url) {
		ResponseEntity<Resource<Machine>> response = client.post(uri(url), createMachine(), MACHINE_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Resource<Machine> machineResource = response.getBody();

		assertNotNull(machineResource);
		assertEquals(2, machineResource.getLinks().size());
		assertTrue(machineResource.getId().getHref().endsWith(machineResource.getContent().getId().toString()));
		assertNotNull(machineResource.getLink(GET_ON_MACHINE));

		return machineResource;
	}

	private Resource<Visitor> signUp(String url) {
		ResponseEntity<Resource<Visitor>> response = client.post(uri(url), createVisitor(), VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Resource<Visitor> visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(2, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(visitorResource.getContent().getId().toString()));
		assertNotNull(visitorResource.getLink(VISITOR_ENTER_PARK));

		return visitorResource;
	}

	private Resource<Visitor> enterPark(String enterParkUrl, Long amusementParkId) {
		ResponseEntity<Resource<Visitor>> response = client.put(uri(enterParkUrl, amusementParkId), 200, VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Resource<Visitor> visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(4, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(visitorResource.getContent().getId().toString()));
		assertNotNull(visitorResource.getLink(VISITOR_LEAVE_PARK));
		assertNotNull(visitorResource.getLink(GET_ON_MACHINE));
		assertNotNull(visitorResource.getLink(ADD_REGISTRY));

		return visitorResource;
	}

	private Resource<Visitor> getOnMachine(String getOnMachineUrl, Long visitorId) {
		ResponseEntity<Resource<Visitor>> response = client.put(uri(getOnMachineUrl, visitorId), null, VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Resource<Visitor> visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(2, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(visitorResource.getContent().getId().toString()));
		assertNotNull(visitorResource.getLink(GET_OFF_MACHINE));

		return visitorResource;
	}

	private Resource<Visitor> getOffMachine(String getOffMachineUrl) {
		ResponseEntity<Resource<Visitor>> response = client.put(uri(getOffMachineUrl), null, VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Resource<Visitor> visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(4, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(visitorResource.getContent().getId().toString()));
		assertNotNull(visitorResource.getLink(VISITOR_LEAVE_PARK));
		assertNotNull(visitorResource.getLink(GET_ON_MACHINE));
		assertNotNull(visitorResource.getLink(ADD_REGISTRY));

		return visitorResource;
	}

	private void addRegistry(String addRegistryUrl) {
		ResponseEntity<Resource<GuestBookRegistry>> response = client.post(uri(addRegistryUrl), OPINION_ON_THE_PARK,
				GUEST_BOOK_REGISTRY_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		Resource<GuestBookRegistry> guestBookRegistryResource = response.getBody();

		assertNotNull(guestBookRegistryResource);
		assertEquals(2, guestBookRegistryResource.getLinks().size());
		assertTrue(guestBookRegistryResource.getId().getHref()
				.endsWith(guestBookRegistryResource.getContent().getId().toString()));
		assertNotNull(guestBookRegistryResource.getLink(ADD_REGISTRY));
	}

	private void leavePark(String leaveParkUrl) {
		ResponseEntity<Void> response = client.put(uri(leaveParkUrl), null, Void.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	private void sellMachine(String machineUrlWithId) {
		ResponseEntity<Void> response = client.delete(uri(machineUrlWithId));

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	private void deletePark(String amusementParkUrlWithId) {
		if (environment.getActiveProfiles().length == 0) {
			assertThrows(() -> client.delete(uri(amusementParkUrlWithId)), HttpClientErrorException.class,
					exception -> {
						assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
						assertEquals(NO_ARCHIVE_SEND_TYPE, exception.getResponseBodyAsString());
					});
		} else {
			ResponseEntity<Void> response = client.delete(uri(amusementParkUrlWithId));

			assertEquals(HttpStatus.OK, response.getStatusCode());
		}
	}

	private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndAddressNullMessage() {
		return exception -> {
			assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
			String errorMessage = exception.getResponseBodyAsString();
			assertTrue(errorMessage.contains("Validation error: "));
			assertTrue(errorMessage.contains("address"));
			assertTrue(errorMessage.contains("null"));
		};
	}

	private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndMachineTooExpensiveMessage() {
		return exception -> {
			assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
			assertEquals(MACHINE_IS_TOO_EXPENSIVE, exception.getResponseBodyAsString());
		};
	}

	private URI uri(String url, Object... uriVariables) {
		return UriComponentsBuilder.fromHttpUrl(url).build(uriVariables);
	}
}
