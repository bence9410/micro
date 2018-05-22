package hu.beni.amusementpark.test.integration;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.MACHINE_IS_TOO_EXPENSIVE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_ARCHIVE_SEND_TYPE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.validationError;
import static hu.beni.amusementpark.constants.FieldNameConstants.ADDRESS;
import static hu.beni.amusementpark.constants.FieldNameConstants.TYPE;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.MUST_BE_ONE_OF;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.NOT_NULL_MESSAGE;
import static hu.beni.amusementpark.helper.MyAssert.assertThrows;
import static hu.beni.clientsupport.Client.uri;
import static hu.beni.clientsupport.ResponseType.AMUSEMENT_PARK_TYPE;
import static hu.beni.clientsupport.ResponseType.GUEST_BOOK_REGISTRY_TYPE;
import static hu.beni.clientsupport.ResponseType.MACHINE_TYPE;
import static hu.beni.clientsupport.ResponseType.PAGED_AMUSEMENT_PARK_TYPE;
import static hu.beni.clientsupport.ResponseType.VISITOR_TYPE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.ADD_REGISTRY;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.AMUSEMENT_PARK;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.GET_OFF_MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.GET_ON_MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.VISITOR_ENTER_PARK;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.VISITOR_LEAVE_PARK;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.VISITOR_SIGN_UP;
import static hu.beni.clientsupport.factory.ValidResourceFactory.createAmusementParkWithAddress;
import static hu.beni.clientsupport.factory.ValidResourceFactory.createMachine;
import static hu.beni.clientsupport.factory.ValidResourceFactory.createVisitor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.helper.MyAssert.ExceptionAsserter;
import hu.beni.clientsupport.Client;
import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;

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

		AmusementParkResource createdAmusementParkResource = createAmusementParkWithAddress();
		createdAmusementParkResource.setCapital(500);

		ResponseEntity<AmusementParkResource> response = client.post(uri(amusementParkUrl),
				createdAmusementParkResource, AMUSEMENT_PARK_TYPE);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		createdAmusementParkResource = response.getBody();
		String machineLinkHref = createdAmusementParkResource.getLink(MACHINE).getHref();

		MachineResource machineResource = createMachine();
		machineResource.setType("asd");

		assertThrows(() -> client.post(uri(machineLinkHref), machineResource, String.class),
				HttpClientErrorException.class, teaPotStatusAndMachineTypeMustBeOneOf());

		machineResource.setType(MachineType.CAROUSEL.toString());
		machineResource.setPrice(2000);

		assertThrows(() -> client.post(uri(machineLinkHref), machineResource, String.class),
				HttpClientErrorException.class, teaPotStatusAndMachineTooExpensiveMessage());

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
			assertEquals(validationError(ADDRESS, NOT_NULL_MESSAGE), exception.getResponseBodyAsString());
		};
	}

	private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndMachineTypeMustBeOneOf() {
		return exception -> {
			assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
			assertEquals(
					validationError(TYPE, String.format(MUST_BE_ONE_OF, Stream.of(MachineType.values())
							.map(value -> value.toString()).collect(Collectors.toSet()))),
					exception.getResponseBodyAsString());
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
