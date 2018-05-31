package hu.beni.tester.async;

import static hu.beni.clientsupport.Client.uri;
import static hu.beni.clientsupport.ResponseType.AMUSEMENT_PARK_TYPE;
import static hu.beni.clientsupport.ResponseType.MACHINE_TYPE;
import static hu.beni.clientsupport.ResponseType.RESOURCES_MACHINE_TYPE;
import static hu.beni.clientsupport.ResponseType.VISITOR_TYPE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.ADD_REGISTRY;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.GET_OFF_MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.GET_ON_MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.MACHINE;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.VISITOR_ENTER_PARK;
import static hu.beni.clientsupport.constants.HATEOASLinkRelConstants.VISITOR_LEAVE_PARK;
import static hu.beni.tester.constant.Constants.AMUSEMENT_PARK_URL;
import static hu.beni.tester.constant.Constants.GUEST_BOOK_REGISTRY_TEXT;
import static hu.beni.tester.constant.Constants.LOGIN_URL;
import static hu.beni.tester.constant.Constants.LOGOUT_URL;
import static hu.beni.tester.constant.Constants.NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK;
import static hu.beni.tester.constant.Constants.NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN;
import static hu.beni.tester.constant.Constants.PASS;
import static hu.beni.tester.constant.Constants.PASSWORD;
import static hu.beni.tester.constant.Constants.USERNAME;
import static hu.beni.tester.constant.Constants.VISITOR_URL;
import static hu.beni.tester.factory.ResourceFactory.createAmusementParkWithAddress;
import static hu.beni.tester.factory.ResourceFactory.createMachine;
import static hu.beni.tester.factory.ResourceFactory.createVisitor;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.TypeReferences.PagedResourcesType;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import hu.beni.clientsupport.Client;
import hu.beni.clientsupport.ResponseType;
import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.VisitorResource;
import hu.beni.tester.dto.DeleteTime;
import hu.beni.tester.dto.SumAndTime;
import hu.beni.tester.dto.VisitorStuffTime;

@Async
@Service
public class AsyncService {

	public static final PagedResourcesType<ResourceSupport> PAGED_TYPE = new PagedResourcesType<ResourceSupport>() {
	};

	public CompletableFuture<Void> login(Client client, String username) {
		client.post(uri(LOGIN_URL), MediaType.APPLICATION_FORM_URLENCODED, createMap(username, PASS), Void.class);
		return CompletableFuture.completedFuture(null);
	}

	private MultiValueMap<String, String> createMap(String username, String password) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(USERNAME, username);
		map.add(PASSWORD, password);
		return map;
	}

	public CompletableFuture<?> logout(Client client) {
		client.post(uri(LOGOUT_URL), null, Void.class);
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture<DeleteTime> deleteAllPark(Client client) {
		List<Long> tenParkTimes = new LinkedList<>();
		long start = now();
		deleteAllOnUrl(client, AMUSEMENT_PARK_URL, tenParkTimes);
		return CompletableFuture.completedFuture(new DeleteTime(millisFrom(start), tenParkTimes));
	}

	private void deleteAllOnUrl(Client client, String url, List<Long> tenTimes) {
		boolean thereIsStillData;
		do {
			long tenStart = now();
			thereIsStillData = getPageDeleteAllFalseIfNoMore(client, url);
			if (thereIsStillData) {
				tenTimes.add(millisFrom(tenStart));
			}
		} while (thereIsStillData);
	}

	private boolean getPageDeleteAllFalseIfNoMore(Client client, String url) {
		Collection<ResourceSupport> data = client.get(uri(url), PAGED_TYPE).getBody().getContent();
		data.stream().map(ResourceSupport::getId).map(Link::getHref).forEach(href -> client.delete(uri(href)));
		return !data.isEmpty();
	}

	public CompletableFuture<Long> createAmusementParksWithMachines(Client client) {
		long start = now();
		createAmusementParks(client).map(this::mapToMachineLinkHref)
				.forEach(machineUrl -> createMachines(client, machineUrl));
		return CompletableFuture.completedFuture(millisFrom(start));
	}

	private Stream<AmusementParkResource> createAmusementParks(Client client) {
		return IntStream.range(0, NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN).mapToObj(i -> createAmusementPark(client));
	}

	private AmusementParkResource createAmusementPark(Client client) {
		return client.post(uri(AMUSEMENT_PARK_URL), createAmusementParkWithAddress(), AMUSEMENT_PARK_TYPE).getBody();
	}

	private String mapToMachineLinkHref(AmusementParkResource amusementParkResource) {
		return amusementParkResource.getLink(MACHINE).getHref();
	}

	private void createMachines(Client client, String machineUrl) {
		IntStream.range(0, NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK).forEach(i -> addMachine(client, machineUrl));
	}

	private void addMachine(Client client, String machineUrl) {
		client.post(uri(machineUrl), createMachine(), MACHINE_TYPE);
	}

	public CompletableFuture<SumAndTime> sumAmusementParksCapital(Client client) {
		long start = now();
		long sum = sum(client, AMUSEMENT_PARK_URL, AmusementParkResource.class, AmusementParkResource::getCapital);
		return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
	}

	private <T> long sum(Client client, String url, Class<T> clazz, ToIntFunction<T> toIntFunction) {
		Optional<String> nextPageUrl = Optional.of(url);
		long sum = 0;
		do {
			PagedResources<T> page = client.get(uri(nextPageUrl.get()), ResponseType.getPagedType(clazz)).getBody();
			nextPageUrl = Optional.ofNullable(page.getNextLink()).map(Link::getHref);
			sum += page.getContent().stream().mapToInt(toIntFunction).sum();
		} while (nextPageUrl.isPresent());

		return sum;
	}

	public CompletableFuture<VisitorStuffTime> visitAllStuffInEveryPark(Client client) {
		List<Long> oneParkTimes = new LinkedList<>();
		List<Long> tenParkTimes = new LinkedList<>();
		long start = now();
		visitAllStuffInEveryPark(client, oneParkTimes, tenParkTimes);
		return CompletableFuture.completedFuture(new VisitorStuffTime(millisFrom(start), tenParkTimes, oneParkTimes));
	}

	private void visitAllStuffInEveryPark(Client client, List<Long> oneParkTimes, List<Long> tenParkTimes) {
		Optional<String> nextPageUrl = Optional.of(AMUSEMENT_PARK_URL);
		VisitorResource visitorResource = client.post(uri(VISITOR_URL), createVisitor(), VISITOR_TYPE).getBody();
		do {
			long tenParkStart = now();
			PagedResources<AmusementParkResource> page = client
					.get(uri(nextPageUrl.get()), ResponseType.getPagedType(AmusementParkResource.class)).getBody();
			nextPageUrl = Optional.ofNullable(page.getNextLink()).map(Link::getHref);
			visitEverythingInParks(client, page.getContent(), visitorResource.getIdentifier(), oneParkTimes);
			tenParkTimes.add(millisFrom(tenParkStart));
		} while (nextPageUrl.isPresent());
	}

	private void visitEverythingInParks(Client client, Collection<AmusementParkResource> amusementParkResources,
			Long visitorId, List<Long> oneParkTimes) {
		amusementParkResources.stream().map(this::mapToEnterParkUrl)
				.forEach(enterParkUrl -> visitEverythingInAPark(client, uri(enterParkUrl, visitorId), oneParkTimes));
	}

	private String mapToEnterParkUrl(AmusementParkResource amusementParkResource) {
		return amusementParkResource.getLink(VISITOR_ENTER_PARK).getHref();
	}

	private void visitEverythingInAPark(Client client, URI enterParkUrl, List<Long> oneParkTimes) {
		long startPark = now();
		VisitorResource visitorResource = client.put(enterParkUrl, null, VISITOR_TYPE).getBody();
		getMachinesAndGetOnAndOff(client, visitorResource);
		addRegistryAndLeave(client, visitorResource);
		oneParkTimes.add(millisFrom(startPark));
	}

	private void getMachinesAndGetOnAndOff(Client client, VisitorResource visitorResource) {
		client.get(uri(visitorResource.getLink(MACHINE).getHref()), RESOURCES_MACHINE_TYPE).getBody().getContent()
				.stream().forEach(machineResource -> getOnAndOffMachine(client,
						machineResource.getLink(GET_ON_MACHINE).getHref(), visitorResource.getIdentifier()));
	}

	private void getOnAndOffMachine(Client client, String getOnMachineUrl, Long visitorId) {
		VisitorResource onMachineVisitor = client.put(uri(getOnMachineUrl, visitorId), null, VISITOR_TYPE).getBody();
		client.put(uri(onMachineVisitor.getLink(GET_OFF_MACHINE).getHref()), null, Void.class);
	}

	private void addRegistryAndLeave(Client client, VisitorResource visitorResource) {
		client.post(uri(visitorResource.getLink(ADD_REGISTRY).getHref()), GUEST_BOOK_REGISTRY_TEXT, Void.class);
		client.put(uri(visitorResource.getLink(VISITOR_LEAVE_PARK).getHref()), null, Void.class);
	}

	public CompletableFuture<SumAndTime> sumVisitorsSpendingMoney(Client client) {
		long start = now();
		long sum = sum(client, VISITOR_URL, VisitorResource.class, VisitorResource::getSpendingMoney);
		return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
	}

	public CompletableFuture<DeleteTime> deleteAllVisitor(Client client) {
		List<Long> tenVisitorTimes = new LinkedList<>();
		long start = now();
		deleteAllOnUrl(client, VISITOR_URL, tenVisitorTimes);
		return CompletableFuture.completedFuture(new DeleteTime(millisFrom(start), tenVisitorTimes));
	}

	private long now() {
		return System.currentTimeMillis();
	}

	private long millisFrom(long start) {
		return now() - start;
	}
}
