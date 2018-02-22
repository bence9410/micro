package hu.beni.tester.async;

import static hu.beni.tester.constant.Constants.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.springframework.hateoas.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import hu.beni.tester.client.AmusementParkClient;
import hu.beni.tester.dto.DeleteTime;
import hu.beni.tester.dto.Page;
import hu.beni.tester.dto.SumAndTime;
import hu.beni.tester.dto.VisitorStuffTime;
import hu.beni.dto.AmusementParkDTO;
import hu.beni.dto.MachineDTO;
import hu.beni.dto.VisitorDTO;
import lombok.RequiredArgsConstructor;

@Async
@Component
@RequiredArgsConstructor
public class AsyncTestSuite {

	private final AmusementParkClient client;
	
	public CompletableFuture<HttpHeaders> login(String username) {
		return CompletableFuture.completedFuture(client.loginAndReturnHeadersWithJSESSIONID(username, PASS));
	}
	
	public CompletableFuture<?> logout(HttpHeaders headers) {
		client.logout(headers);
		return CompletableFuture.completedFuture(null);
	}
	
	public CompletableFuture<DeleteTime> deleteAllPark(HttpHeaders headers){
		Page<Resource<AmusementParkDTO>> page = null;
		int pageIndex = 0;
		List<Long> tenParkTimes = new LinkedList<>();
		long start = now();
		do {
			page = client.getAmusementParks(pageIndex, PAGE_SIZE, headers);
			long tenParkStart = now();
			page.getContent().stream()
				.map(this::parkToContentId)
				.forEach(amusementParkId -> client.deletePark(amusementParkId, headers));
			tenParkTimes.add(millisFrom(tenParkStart));
		}while (!page.isLast());
		return CompletableFuture.completedFuture(new DeleteTime(System.currentTimeMillis() - start, tenParkTimes));
	}
	
	public CompletableFuture<Long> createAmusementParksWithMachines(HttpHeaders headers) {
		long start = now();
		IntStream.range(0, NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN)
			.mapToLong(i -> parkToContentId(client.postAmusementPark(headers)))
			.forEach(amusementParkId -> IntStream.range(0, NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK)
					.forEach(i -> client.postMachine(amusementParkId, headers)));
		return CompletableFuture.completedFuture(millisFrom(start));
	}
	
	public CompletableFuture<SumAndTime> sumAmusementParksCapital(HttpHeaders headers){
		long start = now();
		Page<Resource<AmusementParkDTO>> page = null;
		int pageIndex = 0;
		long sum = 0;
		do {
			page = client.getAmusementParks(pageIndex++, PAGE_SIZE, headers);
			sum += page.getContent().stream()
				.mapToInt(amusementParkResource -> amusementParkResource.getContent().getCapital())
				.sum();
		}while (!page.isLast());
		return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
	}
	
	public CompletableFuture<VisitorStuffTime> visitSomeStuffInEveryPark(HttpHeaders headers){
		List<Long> oneParkTimes = new LinkedList<>();
		long start = now();
		long visitorId = visitorToContentId(client.postVisitor(headers));
		Page<Resource<AmusementParkDTO>> page = null;
		int pageIndex = 0;
		do {
			page = client.getAmusementParks(pageIndex++, PAGE_SIZE, headers);
			page.getContent().stream()
				.map(this::parkToContentId)
				.forEach(amusementParkId -> 
					visitEveryThingInAPark(amusementParkId, visitorId, headers, oneParkTimes));
		} while (!page.isLast());
		return CompletableFuture.completedFuture(new VisitorStuffTime(millisFrom(start), oneParkTimes));
	}
	
	private void visitEveryThingInAPark(Long amusementParkId, Long visitorId, HttpHeaders headers, List<Long> oneParkTimes) {
		long startPark = now();
		client.enterPark(amusementParkId, visitorId, headers);
		client.getMachineIdsByAmusementParkId(amusementParkId, headers).stream()
			.map(this::machineToContentId)
			.forEach(machineId -> {
				client.getOnMachine(amusementParkId, machineId, visitorId, headers);
				client.getOffMachine(amusementParkId, machineId, visitorId, headers);
			});
		client.addGuestBookRegistry(amusementParkId, visitorId, headers);
		client.leavePark(amusementParkId, visitorId, headers);
		oneParkTimes.add(millisFrom(startPark));
	}
	
	public CompletableFuture<SumAndTime> sumVisitorsSpendingMoney(HttpHeaders headers) {
		long start = now();
		Page<Resource<VisitorDTO>> page = null;
		int pageIndex = 0;
		long sum = 0;
		do {
			page = client.getVisitors(pageIndex++, PAGE_SIZE, headers);
			sum += page.getContent().stream()
				.mapToInt(visitorResource -> visitorResource.getContent().getSpendingMoney())
				.sum();
		}while (!page.isLast());
		return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
	}
	
	public CompletableFuture<DeleteTime> deleteAllVisitor(HttpHeaders headers){
		Page<Resource<VisitorDTO>> page = null;
		int pageIndex = 0;
		List<Long> tenVisitorTimes = new LinkedList<>();
		long start = now();
		do {
			page = client.getVisitors(pageIndex, PAGE_SIZE, headers);
			long tenVisitorStart = now();
			page.getContent().stream()
				.map(this::visitorToContentId)
				.forEach(visitorId -> client.deleteVisitor(visitorId, headers));
			tenVisitorTimes.add(millisFrom(tenVisitorStart));
		}while (!page.isLast());
		return CompletableFuture.completedFuture(new DeleteTime(millisFrom(start), tenVisitorTimes));
	}
	
	private long now() {
		return System.currentTimeMillis();
	}
	
	private long millisFrom(long start) {
		return now() - start;
	}
	
	private Long parkToContentId(Resource<AmusementParkDTO> resource) {
		return resource.getContent().getIdentifier();
	}
	
	private Long machineToContentId(Resource<MachineDTO> resource) {
		return resource.getContent().getIdentifier();
	}
	
	private Long visitorToContentId(Resource<VisitorDTO> resource) {
		return resource.getContent().getIdentifier();
	}
}