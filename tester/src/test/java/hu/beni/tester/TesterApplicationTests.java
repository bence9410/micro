package hu.beni.tester;

import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.tester.archive.ArchiveReceiver;
import hu.beni.tester.dto.SumAndTime;
import hu.beni.tester.dto.TimeTo;
import hu.beni.tester.output.ResultLogger;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.properties.DataProperties;
import hu.beni.tester.properties.NumberOfProperties;
import hu.beni.tester.service.AsyncService;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@SpringBootApplication
@Slf4j
public class TesterApplicationTests {

	private int expectedCapitalBeforeVisitorsSum;
	private int expectedCapitalAfterVisitorsSum;
	private int expectedSpendingMoneySum;

	@Autowired
	private ApplicationProperties properties;

	@Autowired
	private ArchiveReceiver archiveReceiver;

	@Autowired
	@Qualifier("admins")
	private List<AsyncService> admins;

	@Autowired
	@Qualifier("visitors")
	private List<AsyncService> visitors;

	private AsyncService admin;

	private TimeTo timeTo = new TimeTo();

	@PostConstruct
	public void init() {
		admin = admins.get(0);

		NumberOfProperties numberOf = properties.getNumberOf();

		DataProperties data = properties.getData();

		int numberOfAdmins = numberOf.getAdmins();
		int numberOfVisitors = numberOf.getVisitors();
		int numberOfParksPerAdmin = numberOf.getAmusementParksPerAdmin();
		int numberOfMachinesPerPark = numberOf.getMachinesPerPark();

		expectedCapitalBeforeVisitorsSum = numberOf.getAdmins() * numberOfParksPerAdmin
				* (data.getAmusementPark().getCapital() - numberOf.getMachinesPerPark() * data.getMachine().getPrice());
		int moneyOneVisitorSpendInAPark = data.getAmusementPark().getEntranceFee()
				+ numberOfMachinesPerPark * data.getMachine().getTicketPrice();
		expectedCapitalAfterVisitorsSum = expectedCapitalBeforeVisitorsSum
				+ numberOfAdmins * moneyOneVisitorSpendInAPark * numberOfAdmins * numberOfParksPerAdmin;
		expectedSpendingMoneySum = (data.getVisitor().getSpendingMoney()
				- (moneyOneVisitorSpendInAPark * numberOfAdmins * numberOfParksPerAdmin)) * numberOfVisitors;

	}

	@Test
	public void test() {

		login();

		clearDB();

		long start = System.currentTimeMillis();

		createAmusementParksWithMachines();

		sumAmusementParksCapitalBeforeVisitorStuff();

		visitorsVisitAllStuffInEveryPark();

		sumAmusementParksCapitalAfterVisitorStuff();

		sumVisitorsSpendingMoney();

		deleteParksAndVisitors();

		waitForArchiveAmusementParks();

		timeTo.setFullRun(System.currentTimeMillis() - start);

		logout();

		log();

	}

	private void login() {
		log.info("login");
		executeAdminsAsyncAndJoin(AsyncService::login);
		executeVisitorsAsyncAndJoin(AsyncService::login);
	}

	private void clearDB() {
		log.info("clearDB");
		executeAdminAndJoin(AsyncService::deleteAllPark);
		executeAdminAndJoin(AsyncService::deleteAllVisitor);
	}

	private void createAmusementParksWithMachines() {
		log.info("createAmusementParksWithMachines");
		timeTo.setCreateAmusementParksWithMachines(
				executeAdminsAsyncAndJoin(AsyncService::createAmusementParksWithMachines));
	}

	private void sumAmusementParksCapitalBeforeVisitorStuff() {
		log.info("sumAmusementParksCapitalBeforeVisitorStuff");
		timeTo.setFindAllParksPagedBeforeVisitorStuff(executeAdminsAsyncJoinAndMap(
				AsyncService::sumAmusementParksCapital, this::checkCapitalSumBeforeVisitorsGetTime));
	}

	private void visitorsVisitAllStuffInEveryPark() {
		log.info("visitorsVisitAllStuffInEveryPark");
		List<Long> wholeTimes = new LinkedList<>();
		List<Long> tenParkTimes = new LinkedList<>();
		List<Long> oneParkTimes = new LinkedList<>();
		executeVisitorsAsyncJoinAndForEach(AsyncService::visitAllStuffInEveryPark, visitorStuffTime -> {
			wholeTimes.add(visitorStuffTime.getWholeTime());
			tenParkTimes.addAll(visitorStuffTime.getTenParkTimes());
			oneParkTimes.addAll(visitorStuffTime.getOneParkTimes());
		});
		timeTo.setWholeVisitorStuff(wholeTimes);
		timeTo.setTenParkVisitorStuff(tenParkTimes);
		timeTo.setOneParkVisitorStuff(oneParkTimes);
	}

	private void sumAmusementParksCapitalAfterVisitorStuff() {
		log.info("sumAmusementParksCapitalAfterVisitorStuff");
		timeTo.setFindAllParksPagedAfterVisitorStuff(executeAdminsAsyncJoinAndMap(
				AsyncService::sumAmusementParksCapital, this::checkCapitalSumAfterVisitorsGetTime));
	}

	private void sumVisitorsSpendingMoney() {
		log.info("sumVisitorsSpendingMoney");
		timeTo.setFindAllVisitorsPaged(executeAdminsAsyncJoinAndMap(AsyncService::sumVisitorsSpendingMoney,
				this::checkSpendingMoneySunGetTime));
	}

	private void deleteParksAndVisitors() {
		log.info("deleteParksAndVisitors");
		timeTo.setDeleteParks(executeAdminAndJoin(AsyncService::deleteAllPark));
		timeTo.setDeleteVisitors(executeAdminAndJoin(AsyncService::deleteAllVisitor));
	}

	private void waitForArchiveAmusementParks() {
		log.info("waitForArchiveAmusementParks");
		try {
			Assert.assertTrue("ArchiveReceiver CountDownLatch timeout before reaching zero.",
					archiveReceiver.getCountDownLatch().await(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void logout() {
		log.info("logout");
		executeAdminsAsyncAndJoin(AsyncService::logout);
		executeVisitorsAsyncAndJoin(AsyncService::logout);
	}

	private void log() {
		log.info("log");
		ResultLogger resultLogger = new ResultLogger(timeTo, properties);
		resultLogger.logToConsole();
		resultLogger.writeToFile();
	}

	private Long checkCapitalSumBeforeVisitorsGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, expectedCapitalBeforeVisitorsSum,
				"Problem with capital sum before visitors!");
	}

	private Long checkCapitalSumAfterVisitorsGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, expectedCapitalAfterVisitorsSum,
				"Problem with capital sum after visitors!");
	}

	private Long checkSpendingMoneySunGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, expectedSpendingMoneySum, "Problem with spending money sum!");
	}

	private Long checkSumAndReturnTime(SumAndTime sumAndTime, long expectedSum, String errorMessage) {
		if (sumAndTime.getSum() != expectedSum) {
			throw new RuntimeException(errorMessage);
		}
		return sumAndTime.getTime();
	}

	private <R> R executeAdminAndJoin(Function<AsyncService, CompletableFuture<R>> asyncMethod) {
		return asyncMethod.apply(admin).join();
	}

	private <R> List<R> executeAdminsAsyncAndJoin(Function<AsyncService, CompletableFuture<R>> asyncMethod) {
		return admins.stream().map(asyncMethod).collect(toList()).stream().map(CompletableFuture::join)
				.collect(toList());
	}

	private <R, T> List<T> executeAdminsAsyncJoinAndMap(Function<AsyncService, CompletableFuture<R>> asyncMethod,
			Function<R, T> asyncResultMapper) {
		return admins.stream().map(asyncMethod).collect(toList()).stream().map(CompletableFuture::join)
				.map(asyncResultMapper).collect(toList());
	}

	private <R> void executeVisitorsAsyncAndJoin(Function<AsyncService, CompletableFuture<R>> asyncMethod) {
		visitors.stream().map(asyncMethod).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
	}

	private <R> void executeVisitorsAsyncJoinAndForEach(Function<AsyncService, CompletableFuture<R>> asyncMethod,
			Consumer<R> asyncResultConsumer) {
		visitors.stream().map(asyncMethod).collect(toList()).stream().map(CompletableFuture::join)
				.forEach(asyncResultConsumer);
	}

}