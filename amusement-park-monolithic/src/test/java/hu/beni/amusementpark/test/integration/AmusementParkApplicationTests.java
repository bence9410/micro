package hu.beni.amusementpark.test.integration;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.MACHINE_IS_TOO_EXPENSIVE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.validationError;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.oneOfMessage;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.beni.amusementpark.helper.MyAssert.assertThrows;
import static hu.beni.clientsupport.Client.uri;
import static hu.beni.clientsupport.ResponseType.AMUSEMENT_PARK_TYPE;
import static hu.beni.clientsupport.ResponseType.GUEST_BOOK_REGISTRY_TYPE;
import static hu.beni.clientsupport.ResponseType.MACHINE_TYPE;
import static hu.beni.clientsupport.ResponseType.VISITOR_TYPE;
import static hu.beni.clientsupport.ResponseType.getPagedType;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.ADD_REGISTRY;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.AMUSEMENT_PARK;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.GET_OFF_MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.GET_ON_MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.LOGIN;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.LOGOUT;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.ME;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.SIGN_UP;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.UPLOAD_MONEY;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.VISITOR_ENTER_PARK;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.VISITOR_LEAVE_PARK;
import static hu.beni.clientsupport.factory.ValidResourceFactory.createAmusementPark;
import static hu.beni.clientsupport.factory.ValidResourceFactory.createMachine;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.TypeReferences.PagedResourcesType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.config.ClientConfig;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.helper.MyAssert.ExceptionAsserter;
import hu.beni.clientsupport.Client;
import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.GuestBookRegistryResource;
import hu.beni.clientsupport.resource.MachineResource;
import hu.beni.clientsupport.resource.VisitorResource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { AmusementParkApplication.class,
		ClientConfig.class })
public class AmusementParkApplicationTests {

	private static Map<String, String> links;

	@Autowired
	private Client client;

	@LocalServerPort
	private int port;

	@PostConstruct
	public void init() {
		links = links == null ? getBaseLinks() : links;
	}

	private Map<String, String> getBaseLinks() {
		return Stream.of(client.get(uri("http://localhost:" + port + "/links"), Link[].class).getBody())
				.collect(toMap(Link::getRel, Link::getHref));
	}

	@Test
	public void signUpAndUploadMoneyTest() {
		logout();

		VisitorResource inputVisitorResource = VisitorResource
				.builder() //@formatter:off
				.email("benike@gmail.com")
				.password("password")
				.confirmPassword("password")
				.dateOfBirth(LocalDate.of(1994, 10, 22)).build(); //@formatter:on

		assertVisitorResource(inputVisitorResource.getEmail(), inputVisitorResource.getDateOfBirth(), 250,
				client.post(uri(links.get(SIGN_UP)), inputVisitorResource, VISITOR_TYPE).getBody());

		client.post(uri(links.get(UPLOAD_MONEY)), 500);

		assertVisitorResource(inputVisitorResource.getEmail(), inputVisitorResource.getDateOfBirth(), 750,
				client.get(uri(links.get(ME)), VISITOR_TYPE).getBody());
	}

	private void assertVisitorResource(String email, LocalDate dateOfBirth, Integer spendingMoney,
			VisitorResource actualVisitorResource) {
		assertEquals(email, actualVisitorResource.getEmail());
		assertEquals(dateOfBirth, actualVisitorResource.getDateOfBirth());
		assertEquals(spendingMoney.intValue(), actualVisitorResource.getSpendingMoney().intValue());
		assertEquals("ROLE_VISITOR", actualVisitorResource.getAuthority());
		assertNull(actualVisitorResource.getPassword());
		assertNull(actualVisitorResource.getConfirmPassword());
		assertEquals(3, actualVisitorResource.getLinks().size());
		assertNotNull(actualVisitorResource.getId().getHref());
		assertNotNull(actualVisitorResource.getLink(VISITOR_ENTER_PARK).getHref());
		assertNotNull(actualVisitorResource.getLink(AMUSEMENT_PARK).getHref());
	}

	@Test
	public void pageTest() {
		loginAsAdmin("bence@gmail.com", "password");

		PagedResourcesType<AmusementParkResource> responseType = getPagedType(AmusementParkResource.class);

		ResponseEntity<PagedResources<AmusementParkResource>> response = client.get(uri(links.get(AMUSEMENT_PARK)),
				responseType);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		PagedResources<AmusementParkResource> page = response.getBody();
		assertEquals(1, page.getLinks().size());
		assertNotNull(page.getId());

		IntStream.range(0, 11).forEach(i -> postAmusementPark());

		response = client.get(uri(links.get(AMUSEMENT_PARK)), responseType);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(4, page.getLinks().size());
		assertNotNull(page.getLink("last"));

		response = client.get(uri(page.getLink("last").getHref()), responseType);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(4, page.getLinks().size());

		response = client.get(uri(links.get(AMUSEMENT_PARK) + "?input=" + encode("{\"name\":\"a\"}")), responseType);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(4, page.getLinks().size());
		assertNotNull(page.getLink("last"));

		response = client.get(uri(links.get(AMUSEMENT_PARK) + "?input=" + encode("{\"name\":\"x\"}")), responseType);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(1, page.getLinks().size());

	}

	@Test
	public void positiveTest() {
		VisitorResource visitorResource = loginAsAdmin("bence@gmail.com", "password");

		AmusementParkResource amusementParkResource = postAmusementPark();

		MachineResource machineResource = addMachine(amusementParkResource.getLink(MACHINE).getHref());

		visitorResource = enterPark(visitorResource.getLink(VISITOR_ENTER_PARK).getHref(),
				amusementParkResource.getIdentifier());

		visitorResource = getOnMachine(machineResource.getLink(GET_ON_MACHINE).getHref());

		visitorResource = getOffMachine(visitorResource.getLink(GET_OFF_MACHINE).getHref());

		addRegistry(visitorResource.getLink(ADD_REGISTRY).getHref());

		leavePark(visitorResource.getLink(VISITOR_LEAVE_PARK).getHref());

		sellMachine(machineResource.getId().getHref());

		deletePark(amusementParkResource.getId().getHref());
	}

	@Test
	public void negativeTest() {
		loginAsAdmin("bence@gmail.com", "password");

		AmusementParkResource amusementParkResource = createAmusementPark();
		amusementParkResource.setEntranceFee(0);

		assertThrows(() -> client.post(uri(links.get(AMUSEMENT_PARK)), amusementParkResource, String.class),
				HttpClientErrorException.class, teaPotStatusAndAddressNullMessage());

		AmusementParkResource createdAmusementParkResource = createAmusementPark();
		createdAmusementParkResource.setCapital(500);

		ResponseEntity<AmusementParkResource> response = client.post(uri(links.get(AMUSEMENT_PARK)),
				createdAmusementParkResource, AMUSEMENT_PARK_TYPE);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		createdAmusementParkResource = response.getBody();
		String machineLinkHref = createdAmusementParkResource.getLink(MACHINE).getHref();

		MachineResource machineResource = createMachine();
		machineResource.setType("asd");

		assertThrows(() -> client.post(uri(machineLinkHref), machineResource, String.class),
				HttpClientErrorException.class, teaPotStatusAndMachineTypeMustMatch());

		machineResource.setType(MachineType.CAROUSEL.toString());
		machineResource.setPrice(2000);

		assertThrows(() -> client.post(uri(machineLinkHref), machineResource, String.class),
				HttpClientErrorException.class, teaPotStatusAndMachineTooExpensiveMessage());
	}

	private VisitorResource loginAsAdmin(String email, String password) {
		logout();
		ResponseEntity<VisitorResource> response = client.post(uri(links.get(LOGIN)), createMap(email, password),
				VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().getFirst("Set-Cookie").contains("JSESSIONID="));

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(3, visitorResource.getLinks().size());
		assertNotNull(visitorResource.getId().getHref());
		assertNotNull(visitorResource.getLink(VISITOR_ENTER_PARK));
		assertNotNull(visitorResource.getLink(AMUSEMENT_PARK));

		assertEquals(email, visitorResource.getEmail());
		assertEquals("ROLE_ADMIN", visitorResource.getAuthority());

		return visitorResource;
	}

	private MultiValueMap<String, String> createMap(String username, String password) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("email", username);
		map.add("password", password);
		return map;
	}

	private void logout() {
		ResponseEntity<Void> response = client.post(uri(links.get(LOGOUT)));

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(response.getHeaders().getLocation().toString().endsWith(Integer.toString(port) + "/"));

		testRedirectToLoginPage();

		restTemplateFollowsRedirectOnGet();
	}

	private void testRedirectToLoginPage() {
		ResponseEntity<Void> response = client.post(uri(links.get(AMUSEMENT_PARK)));

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(response.getHeaders().getLocation().toString().endsWith(Integer.toString(port) + "/"));
	}

	private void restTemplateFollowsRedirectOnGet() {
		ResponseEntity<String> loginPageResponse = client.get(uri(links.get(AMUSEMENT_PARK)), String.class);

		assertEquals(HttpStatus.OK, loginPageResponse.getStatusCode());
		assertTrue(loginPageResponse.getBody().length() > 450);
	}

	private AmusementParkResource postAmusementPark() {
		AmusementParkResource amusementParkResource = createAmusementPark();

		ResponseEntity<AmusementParkResource> response = client.post(uri(links.get(AMUSEMENT_PARK)),
				amusementParkResource, AMUSEMENT_PARK_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		AmusementParkResource responseAmusementParkResource = response.getBody();

		assertNotNull(responseAmusementParkResource);
		assertEquals(4, responseAmusementParkResource.getLinks().size());
		assertTrue(responseAmusementParkResource.getId().getHref()
				.endsWith(responseAmusementParkResource.getIdentifier().toString()));
		assertNotNull(responseAmusementParkResource.getLink(MACHINE));
		assertNotNull(responseAmusementParkResource.getLink(SIGN_UP));
		assertNotNull(responseAmusementParkResource.getLink(VISITOR_ENTER_PARK));

		amusementParkResource.setIdentifier(responseAmusementParkResource.getIdentifier());
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

	private VisitorResource enterPark(String enterParkUrl, Long amusementParkId) {
		ResponseEntity<VisitorResource> response = client.put(uri(enterParkUrl, amusementParkId), 200, VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(5, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(ME));
		assertNotNull(visitorResource.getLink(VISITOR_LEAVE_PARK));
		assertNotNull(visitorResource.getLink(GET_ON_MACHINE));
		assertNotNull(visitorResource.getLink(ADD_REGISTRY));
		assertNotNull(visitorResource.getLink(MACHINE));

		return visitorResource;
	}

	private VisitorResource getOnMachine(String getOnMachineUrl) {
		ResponseEntity<VisitorResource> response = client.put(uri(getOnMachineUrl), VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(2, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(ME));
		assertNotNull(visitorResource.getLink(GET_OFF_MACHINE));

		return visitorResource;
	}

	private VisitorResource getOffMachine(String getOffMachineUrl) {
		ResponseEntity<VisitorResource> response = client.put(uri(getOffMachineUrl), VISITOR_TYPE);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(5, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(ME));
		assertNotNull(visitorResource.getLink(VISITOR_LEAVE_PARK));
		assertNotNull(visitorResource.getLink(GET_ON_MACHINE));
		assertNotNull(visitorResource.getLink(ADD_REGISTRY));
		assertNotNull(visitorResource.getLink(MACHINE));

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
		ResponseEntity<Void> response = client.put(uri(leaveParkUrl));
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	private void sellMachine(String machineUrlWithId) {
		ResponseEntity<Void> response = client.delete(uri(machineUrlWithId));
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	private void deletePark(String amusementParkUrlWithId) {
		ResponseEntity<Void> response = client.delete(uri(amusementParkUrlWithId));
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndAddressNullMessage() {
		return exception -> {
			assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
			assertEquals(validationError("entranceFee", rangeMessage(5, 200)), exception.getResponseBodyAsString());
		};
	}

	private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndMachineTypeMustMatch() {
		return exception -> {
			assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
			assertEquals(
					validationError("type", oneOfMessage(
							Stream.of(MachineType.values()).map(MachineType::toString).collect(toSet()).toString())),
					exception.getResponseBodyAsString());
		};
	}

	private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndMachineTooExpensiveMessage() {
		return exception -> {
			assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
			assertEquals(MACHINE_IS_TOO_EXPENSIVE, exception.getResponseBodyAsString());
		};
	}

	private String encode(String input) {
		try {
			return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new AmusementParkException("Wrong input!", e);
		}
	}
}
