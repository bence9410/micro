package hu.beni.tester.async;

import static hu.beni.clientsupport.Client.uri;
import static hu.beni.clientsupport.ResponseType.AMUSEMENT_PARK_TYPE;
import static hu.beni.clientsupport.ResponseType.MACHINE_TYPE;
import static hu.beni.clientsupport.ResponseType.PAGED_AMUSEMENT_PARK_TYPE;
import static hu.beni.clientsupport.ResponseType.PAGED_VISITOR_TYPE;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import hu.beni.clientsupport.Client;
import hu.beni.clientsupport.resource.AmusementParkResource;
import hu.beni.clientsupport.resource.VisitorResource;
import hu.beni.tester.dto.DeleteTime;
import hu.beni.tester.dto.SumAndTime;
import hu.beni.tester.dto.VisitorStuffTime;

@Async
@Service
public class AsyncService {

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
		boolean amusementParksInDB = true;
		long start = now();
		do {
			long tenParkStart = now();
			PagedResources<AmusementParkResource> page = client.get(uri(AMUSEMENT_PARK_URL), PAGED_AMUSEMENT_PARK_TYPE)
					.getBody();
			amusementParksInDB = page.getMetadata().getTotalElements() != 0;
			page.getContent().stream().map(AmusementParkResource::getId).map(Link::getHref)
					.forEach(href -> client.delete(uri(href)));
			if (amusementParksInDB) {
				tenParkTimes.add(millisFrom(tenParkStart));
			}
		} while (amusementParksInDB);
		return CompletableFuture.completedFuture(new DeleteTime(millisFrom(start), tenParkTimes));
	}

	public CompletableFuture<Long> createAmusementParksWithMachines(Client client) {
		long start = now();
		IntStream.range(0, NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN)
				.mapToObj(i -> client.post(uri(AMUSEMENT_PARK_URL), createAmusementParkWithAddress(),
						AMUSEMENT_PARK_TYPE))
				.map(response -> response.getBody().getLink(MACHINE).getHref())
				.forEach(machineLink -> IntStream.range(0, NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK)
						.forEach(i -> client.post(uri(machineLink), createMachine(), MACHINE_TYPE)));
		return CompletableFuture.completedFuture(millisFrom(start));
	}

	public CompletableFuture<SumAndTime> sumAmusementParksCapital(Client client) {
		String nextPageUrl = AMUSEMENT_PARK_URL;
		long sum = 0;
		long start = now();
		do {
			PagedResources<AmusementParkResource> page = client.get(uri(nextPageUrl), PAGED_AMUSEMENT_PARK_TYPE)
					.getBody();
			nextPageUrl = Optional.ofNullable(page.getNextLink()).map(Link::getHref).orElse(null);
			sum += page.getContent().stream().mapToInt(amusementParkResource -> amusementParkResource.getCapital())
					.sum();
		} while (nextPageUrl != null);
		return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
	}

	public CompletableFuture<VisitorStuffTime> visitSomeStuffInEveryPark(Client client) {
		List<Long> tenParkTimes = new LinkedList<>();
		List<Long> oneParkTimes = new LinkedList<>();
		String nextPageUrl = AMUSEMENT_PARK_URL;
		long start = now();
		VisitorResource visitorResource = client.post(uri(VISITOR_URL), createVisitor(), VISITOR_TYPE).getBody();
		do {
			long tenParkStart = now();
			PagedResources<AmusementParkResource> page = client.get(uri(nextPageUrl), PAGED_AMUSEMENT_PARK_TYPE)
					.getBody();
			nextPageUrl = Optional.ofNullable(page.getNextLink()).map(Link::getHref).orElse(null);
			page.getContent().stream()
					.map(amusementParkResource -> amusementParkResource.getLink(VISITOR_ENTER_PARK).getHref())
					.forEach(enterParkUrl -> visitEverythingInAPark(client,
							uri(enterParkUrl, visitorResource.getIdentifier()).toString(), oneParkTimes));
			tenParkTimes.add(millisFrom(tenParkStart));
		} while (nextPageUrl != null);
		return CompletableFuture.completedFuture(new VisitorStuffTime(millisFrom(start), tenParkTimes, oneParkTimes));
	}

	private void visitEverythingInAPark(Client client, String enterParkUrl, List<Long> oneParkTimes) {
		long startPark = now();
		VisitorResource visitorResource = client.put(uri(enterParkUrl), null, VISITOR_TYPE).getBody();
		client.get(uri(visitorResource.getLink(MACHINE).getHref()), RESOURCES_MACHINE_TYPE).getBody().getContent()
				.stream().forEach(machineResource -> {
					VisitorResource onMachineVisitor = client.put(
							uri(machineResource.getLink(GET_ON_MACHINE).getHref(), visitorResource.getIdentifier()),
							null, VISITOR_TYPE).getBody();
					client.put(uri(onMachineVisitor.getLink(GET_OFF_MACHINE).getHref()), null, Void.class);
				});
		client.post(uri(visitorResource.getLink(ADD_REGISTRY).getHref()), GUEST_BOOK_REGISTRY_TEXT, Void.class);
		client.put(uri(visitorResource.getLink(VISITOR_LEAVE_PARK).getHref()), null, Void.class);
		oneParkTimes.add(millisFrom(startPark));
	}

	public CompletableFuture<SumAndTime> sumVisitorsSpendingMoney(Client client) {
		String nextPageUrl = VISITOR_URL;
		long sum = 0;
		long start = now();
		do {
			PagedResources<VisitorResource> page = client.get(uri(nextPageUrl), PAGED_VISITOR_TYPE).getBody();
			nextPageUrl = Optional.ofNullable(page.getNextLink()).map(Link::getHref).orElse(null);
			sum += page.getContent().stream().mapToInt(visitorResource -> visitorResource.getSpendingMoney()).sum();
		} while (nextPageUrl != null);
		return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
	}

	public CompletableFuture<DeleteTime> deleteAllVisitor(Client client) {
		List<Long> tenVisitorTimes = new LinkedList<>();
		boolean visitorsInDB = true;
		long start = now();
		do {
			long tenVisitorStart = now();
			PagedResources<VisitorResource> page = client.get(uri(VISITOR_URL), PAGED_VISITOR_TYPE).getBody();
			visitorsInDB = page.getMetadata().getTotalElements() != 0;
			page.getContent().stream().map(visitorResource -> visitorResource.getId().getHref())
					.forEach(deleteUrl -> client.delete(uri(deleteUrl)));
			if (visitorsInDB) {
				tenVisitorTimes.add(millisFrom(tenVisitorStart));
			}
		} while (visitorsInDB);
		return CompletableFuture.completedFuture(new DeleteTime(millisFrom(start), tenVisitorTimes));
	}

	private long now() {
		return System.currentTimeMillis();
	}

	private long millisFrom(long start) {
		return now() - start;
	}
}
