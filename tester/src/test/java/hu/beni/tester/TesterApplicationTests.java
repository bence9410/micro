package hu.beni.tester;

import static hu.beni.tester.constant.Constants.NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK;
import static hu.beni.tester.constant.Constants.NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN;
import static hu.beni.tester.factory.ResourceFactory.AMUSEMENT_PARK_CAPITAL;
import static hu.beni.tester.factory.ResourceFactory.AMUSEMENT_PARK_ENTRANCE_FEE;
import static hu.beni.tester.factory.ResourceFactory.MACHINE_PRICE;
import static hu.beni.tester.factory.ResourceFactory.MACHINE_TICKET_PRICE;
import static hu.beni.tester.factory.ResourceFactory.VISITOR_SPENDING_MONEY;
import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
import hu.beni.tester.service.AsyncService;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@SpringBootApplication
@Slf4j
public class TesterApplicationTests {

	public static final int NUMBER_OF_ADMINS = 5;
	public static final int NUMBER_OF_USERS = 5;

	private static final int EXPECTED_CAPITAL_BEFORE_VISITORS_SUM;
	private static final int EXPECTED_CAPITAL_AFTER_VISITORS_SUM;
	private static final int EXPECTED_SPENDING_MONEY_SUM;

	static {
		EXPECTED_CAPITAL_BEFORE_VISITORS_SUM = NUMBER_OF_ADMINS * NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN
				* (AMUSEMENT_PARK_CAPITAL - NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK * MACHINE_PRICE);
		int moneyOneVisitorSpendInAPark = AMUSEMENT_PARK_ENTRANCE_FEE
				+ NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK * MACHINE_TICKET_PRICE;
		EXPECTED_CAPITAL_AFTER_VISITORS_SUM = EXPECTED_CAPITAL_BEFORE_VISITORS_SUM + NUMBER_OF_USERS
				* moneyOneVisitorSpendInAPark * NUMBER_OF_ADMINS * NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN;
		EXPECTED_SPENDING_MONEY_SUM = (VISITOR_SPENDING_MONEY
				- (moneyOneVisitorSpendInAPark * NUMBER_OF_ADMINS * NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN))
				* NUMBER_OF_USERS;
	}

	@Autowired
	private ArchiveReceiver archiveReceiver;

	@Autowired
	@Qualifier("admins")
	private List<AsyncService> admins;

	@Autowired
	@Qualifier("users")
	private List<AsyncService> users;

	private AsyncService admin;

	private TimeTo timeTo;
	private long start;

	@Before
	public void setUp() {
		start = System.currentTimeMillis();
		log.info("login");

		admin = admins.get(0);

		executeAdminsAsyncAndJoin(AsyncService::login);

		executeUsersAsyncAndJoin(AsyncService::login);

		timeTo = new TimeTo();
	}

	@After
	public void tearDown() {
		log.info("logout");
		executeAdminsAsyncAndJoin(AsyncService::logout);
		executeUsersAsyncAndJoin(AsyncService::logout);
		log.info("log");
		timeTo.setFullRun(System.currentTimeMillis() - start);
		ResultLogger resultLogger = new ResultLogger(timeTo);
		resultLogger.logToConsole();
		resultLogger.writeToFile();
	}

	@Test
	public void test() {

		clearDB();

		createAmusementParksWithMachines();

		sumAmusementParksCapitalBeforeVisitorStuff();

		visitorsVisitAllStuffInEveryPark();

		sumAmusementParksCapitalAfterVisitorStuff();

		sumVisitorsSpendingMoney();

		deleteParksAndVisitors();

		waitForArchiveAmusementParks();

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
		executeUsersAsyncJoinAndForEach(AsyncService::visitAllStuffInEveryPark, visitorStuffTime -> {
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

	private Long checkCapitalSumBeforeVisitorsGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, EXPECTED_CAPITAL_BEFORE_VISITORS_SUM,
				"Problem with capital sum before visitors!");
	}

	private Long checkCapitalSumAfterVisitorsGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, EXPECTED_CAPITAL_AFTER_VISITORS_SUM,
				"Problem with capital sum after visitors!");
	}

	private Long checkSpendingMoneySunGetTime(SumAndTime sumAndTime) {
		return checkSumAndReturnTime(sumAndTime, EXPECTED_SPENDING_MONEY_SUM, "Problem with spending money sum!");
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

	private <R> void executeUsersAsyncAndJoin(Function<AsyncService, CompletableFuture<R>> asyncMethod) {
		users.stream().map(asyncMethod).collect(toList()).stream().map(CompletableFuture::join).collect(toList());
	}

	private <R> void executeUsersAsyncJoinAndForEach(Function<AsyncService, CompletableFuture<R>> asyncMethod,
			Consumer<R> asyncResultConsumer) {
		users.stream().map(asyncMethod).collect(toList()).stream().map(CompletableFuture::join)
				.forEach(asyncResultConsumer);
	}

}