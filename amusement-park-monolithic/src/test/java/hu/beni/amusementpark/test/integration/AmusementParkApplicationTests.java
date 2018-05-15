package hu.beni.amusementpark.test.integration;

import hu.beni.clientsupport.Client;
import hu.beni.amusementpark.helper.MyAssert.ExceptionAsserter;
import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.helper.MyAssert.assertThrows;
import static hu.beni.clientsupport.factory.ValidResourceFactory.*;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static hu.beni.clientsupport.ResponseType.*;
import static hu.beni.clientsupport.Client.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AmusementParkApplicationTests {

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
	public void pageTest() {

		login("admin", "pass");

		ResponseEntity<PagedResources<AmusementParkResource>> response = client.get(uri(amusementParkUrl),
				PAGED_AMUSEMENT_PARK_TYPE);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		PagedResources<AmusementParkResource> page = response.getBody();
		assertEquals(1, page.getLinks().size());
		assertNotNull(page.getId());

		IntStream.range(0, 11).forEach(i -> createAmusementPark());

		response = client.get(uri(amusementParkUrl), PAGED_AMUSEMENT_PARK_TYPE);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(4, page.getLinks().size());
		assertNotNull(page.getLink("last"));

		response = client.get(uri(page.getLink("last").getHref()), PAGED_AMUSEMENT_PARK_TYPE);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(4, page.getLinks().size());
	}

	@Test
	public void positiveTest() {
		login("admin", "pass");

		AmusementParkResource amusementParkResource = createAmusementPark();

		MachineResource machineResource = addMachine(amusementParkResource.getLink(MACHINE).getHref());

		VisitorResource visitorResource = signUp(amusementParkResource.getLink(VISITOR_SIGN_UP).getHref());

		visitorResource = enterPark(visitorResource.getLink(VISITOR_ENTER_PARK).getHref(),
				amusementParkResource.getIdentifier());

		visitorResource = getOnMachine(machineResource.getLink(GET_ON_MACHINE).getHref(),
				visitorResource.getIdentifier());

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

		AmusementParkResource amusementParkResource = createAmusementParkWithAddress();
		amusementParkResource.setAddress(null);

		assertThrows(() -> client.post(uri(amusementParkUrl), amusementParkResource, String.class),
				HttpClientErrorException.class, teaPotStatusAndAddressNullMessage());

		AmusementParkResource createdAmusementParkResource = createAmusementPark();

		MachineResource machineResource = createMachine();
		machineResource.setPrice(4000);

		assertThrows(() -> client.post(uri(createdAmusementParkResource.getLink(MACHINE).getHref()), machineResource,
				String.class), HttpClientErrorException.class, teaPotStatusAndMachineTooExpensiveMessage());

		logout();
	}

	private void login(String username, String password) {
		logout();
		
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

	private AmusementParkResource createAmusementPark() {
		AmusementParkResource amusementParkResource = createAmusementParkWithAddress();

		ResponseEntity<AmusementParkResource> response = client.post(uri(amusementParkUrl), amusementParkResource,
				AMUSEMENT_PARK_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		AmusementParkResource responseAmusementParkResource = response.getBody();

		assertNotNull(responseAmusementParkResource);
		assertEquals(4, responseAmusementParkResource.getLinks().size());
		assertTrue(responseAmusementParkResource.getId().getHref()
				.endsWith(responseAmusementParkResource.getIdentifier().toString()));
		assertNotNull(responseAmusementParkResource.getLink(MACHINE));
		assertNotNull(responseAmusementParkResource.getLink(VISITOR_SIGN_UP));
		assertNotNull(responseAmusementParkResource.getLink(VISITOR_ENTER_PARK));

		amusementParkResource.setIdentifier(responseAmusementParkResource.getIdentifier());
		amusementParkResource.getAddress().setIdentifier(responseAmusementParkResource.getAddress().getIdentifier());
		amusementParkResource.add(responseAmusementParkResource.getLinks());
		assertEquals(amusementParkResource, responseAmusementParkResource);

		return responseAmusementParkResource;
	}

	private MachineResource addMachine(String url) {
		ResponseEntity<MachineResource> response = client.post(uri(url), createMachine(), MACHINE_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		MachineResource machineResource = response.getBody();

		assertNotNull(machineResource);
		assertEquals(2, machineResource.getLinks().size());
		assertTrue(machineResource.getId().getHref().endsWith(machineResource.getIdentifier().toString()));
		assertNotNull(machineResource.getLink(GET_ON_MACHINE));

		return machineResource;
	}

	private VisitorResource signUp(String url) {
		ResponseEntity<VisitorResource> response = client.post(uri(url), createVisitor(), VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(3, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(visitorResource.getIdentifier().toString()));
		assertNotNull(visitorResource.getLink(VISITOR_ENTER_PARK));
		assertNotNull(visitorResource.getLink(AMUSEMENT_PARK));

		return visitorResource;
	}

	private VisitorResource enterPark(String enterParkUrl, Long amusementParkId) {
		ResponseEntity<VisitorResource> response = client.put(uri(enterParkUrl, amusementParkId), 200, VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(4, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(visitorResource.getIdentifier().toString()));
		assertNotNull(visitorResource.getLink(VISITOR_LEAVE_PARK));
		assertNotNull(visitorResource.getLink(GET_ON_MACHINE));
		assertNotNull(visitorResource.getLink(ADD_REGISTRY));

		return visitorResource;
	}

	private VisitorResource getOnMachine(String getOnMachineUrl, Long visitorId) {
		ResponseEntity<VisitorResource> response = client.put(uri(getOnMachineUrl, visitorId), null, VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(2, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(visitorResource.getIdentifier().toString()));
		assertNotNull(visitorResource.getLink(GET_OFF_MACHINE));

		return visitorResource;
	}

	private VisitorResource getOffMachine(String getOffMachineUrl) {
		ResponseEntity<VisitorResource> response = client.put(uri(getOffMachineUrl), null, VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(4, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(visitorResource.getIdentifier().toString()));
		assertNotNull(visitorResource.getLink(VISITOR_LEAVE_PARK));
		assertNotNull(visitorResource.getLink(GET_ON_MACHINE));
		assertNotNull(visitorResource.getLink(ADD_REGISTRY));

		return visitorResource;
	}

	private void addRegistry(String addRegistryUrl) {
		ResponseEntity<GuestBookRegistryResource> response = client.post(uri(addRegistryUrl), OPINION_ON_THE_PARK,
				GUEST_BOOK_REGISTRY_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		GuestBookRegistryResource guestBookRegistryResource = response.getBody();

		assertNotNull(guestBookRegistryResource);
		assertEquals(2, guestBookRegistryResource.getLinks().size());
		assertTrue(guestBookRegistryResource.getId().getHref()
				.endsWith(guestBookRegistryResource.getIdentifier().toString()));
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
					teaPotStatusNoArchiveSendTypeMessage());
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

	private ExceptionAsserter<HttpClientErrorException> teaPotStatusNoArchiveSendTypeMessage() {
		return exception -> {
			assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
			assertEquals(NO_ARCHIVE_SEND_TYPE, exception.getResponseBodyAsString());
		};
	}
}
