package hu.beni.tester;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.tester.async.AsyncTestSuite;
import hu.beni.tester.dto.SumAndTime;
import hu.beni.tester.dto.TimeTo;
import hu.beni.tester.output.ResultLogger;
import lombok.extern.slf4j.Slf4j;

import static hu.beni.tester.constant.Constants.*;
import static java.util.stream.Collectors.toList;
import static hu.beni.tester.factory.ValidDTOFactory.*;

import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@SpringBootApplication
@Slf4j
public class TesterApplicationTests {
	
	public static final int NUMBER_OF_ADMINS = 5;
	public static final int NUMBER_OF_USERS = 10;
	
	private static final int EXPECTED_CAPITAL_BEFORE_VISITORS_SUM;
	private static final int EXPECTED_CAPITAL_AFTER_VISITORS_SUM;
	private static final int EXPECTED_SPENDING_MONEY_SUM;
	
	static {
		EXPECTED_CAPITAL_BEFORE_VISITORS_SUM = NUMBER_OF_ADMINS * NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN * (AMUSEMENT_PARK_CAPITAL - NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK * MACHINE_PRICE);
		int moneyOneVisitorSpendInAPark = AMUSEMENT_PARK_ENTRANCE_FEE + NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK * MACHINE_TICKET_PRICE;
		EXPECTED_CAPITAL_AFTER_VISITORS_SUM = EXPECTED_CAPITAL_BEFORE_VISITORS_SUM + NUMBER_OF_USERS * moneyOneVisitorSpendInAPark * NUMBER_OF_ADMINS * NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN;
		EXPECTED_SPENDING_MONEY_SUM = (VISITOR_SPENDING_MONEY - (moneyOneVisitorSpendInAPark * NUMBER_OF_ADMINS * NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN)) * NUMBER_OF_USERS;
	}
	
	@Autowired
	public AsyncTestSuite async;
	
	private List<HttpHeaders> admins;
	private List<HttpHeaders> users;
	
	private TimeTo timeTo;
	
	private static Stream<String> createUsernameStream(int endExclusive, IntFunction<String> function) {
		return IntStream.range(0, endExclusive).mapToObj(function);
	}
	
	@Before
	public void setUp() {
		log.info("login");
		admins = executeAsyncAndGet(createUsernameStream(NUMBER_OF_ADMINS, this::createAdminUsername), async::login);
		users = executeAsyncAndGet(createUsernameStream(NUMBER_OF_USERS, this::createUserUsername), async::login);
		timeTo = new TimeTo();
	}
	
	@After
	public void tearDown() {
		log.info("logout");
		admins.stream().forEach(async::logout);
		users.stream().forEach(async::logout);
		log.info("log");
		ResultLogger resultLogger = new ResultLogger(timeTo);
		resultLogger.logToConsole();
		resultLogger.writeToFile();
	}

	@Test
	public void test() {
		
		clearDB();
		
		createAmusementParksWithMachines();
		
		sumAmusementParksCapitalBeforeVisitorStuff();
				
		visitorsVisitSomeStuffInEveryPark();
		
		sumAmusementParksCapitalAfterVisitorStuff();
		
		sumVisitorsSpendingMoney();
		
		deleteParksAndVisitors();
		
	}
	
	private void clearDB() {
		log.info("clearDB");
		extract(async.deleteAllPark(admins.get(0)));
		extract(async.deleteAllVisitor(admins.get(0)));
	}
	
	private void createAmusementParksWithMachines() {
		log.info("createAmusementParksWithMachines");
		timeTo.setCreateAmusementParksWithMachines(executeAsyncAndGet(admins, async::createAmusementParksWithMachines));
	}
	
	private void sumAmusementParksCapitalBeforeVisitorStuff() {
		log.info("sumAmusementParksCapitalBeforeVisitorStuff");
		timeTo.setFindAllParksPagedBeforeVisitorStuff(
				map(executeAsyncAndGet(admins, async::sumAmusementParksCapital), 
						this::checkCapitalSumBeforeVisitorsGetTime));
	}
	
	private void visitorsVisitSomeStuffInEveryPark() {
		log.info("visitorsVisitSomeStuffInEveryPark");
		List<Long> wholeTimes = new LinkedList<>();
		List<Long> oneParkTimes = new LinkedList<>();
		executeAsyncAndGet(users, async::visitSomeStuffInEveryPark)
			.forEach(VisitorStuffTime -> {
				wholeTimes.add(VisitorStuffTime.getWholeTime());
				oneParkTimes.addAll(VisitorStuffTime.getOneParkTimes());
			});
		timeTo.setWholeVisitorStuff(wholeTimes);
		timeTo.setOneParkVisitorStuff(oneParkTimes);
	}
	
	private void sumAmusementParksCapitalAfterVisitorStuff() {
		log.info("sumAmusementParksCapitalAfterVisitorStuff");
		timeTo.setFindAllParksPagedAfterVisitorStuff(
				map(executeAsyncAndGet(admins, async::sumAmusementParksCapital),
						this::checkCapitalSumAfterVisitorsGetTime));
	}
	
	private void sumVisitorsSpendingMoney() {
		log.info("sumVisitorsSpendingMoney");
		timeTo.setFindAllVisitorsPaged(
				map(executeAsyncAndGet(admins, async::sumVisitorsSpendingMoney),
						this::checkSpendingMoneySunGetTime));
	}
	
	private void deleteParksAndVisitors() {
		log.info("deleteParksAndVisitors");
		timeTo.setDeleteParks(extract(async.deleteAllPark(admins.get(0))));
		timeTo.setDeleteVisitors(extract(async.deleteAllVisitor(admins.get(0))));
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
		return checkSumAndReturnTime(sumAndTime, EXPECTED_SPENDING_MONEY_SUM,
				"Problem with spending money sum!");
	}
	
	private Long checkSumAndReturnTime(SumAndTime sumAndTime, long expectedSum, String errorMessage) {
		if (sumAndTime.getSum() != expectedSum) {
			throw new RuntimeException(errorMessage);
		}
		return sumAndTime.getTime();
	}
	
	private <T, R> List<R> executeAsyncAndGet(List<T> list, Function<T, CompletableFuture<R>> function) {
		return executeAsyncAndGet(list.stream(), function);
	}
	
	private <T, R> List<R> executeAsyncAndGet(Stream<T> stream, Function<T, CompletableFuture<R>> function) {
		return stream.map(function).collect(toList()).stream().map(this::extract).collect(toList());
	}
	
	private <T, R> List<R> map(List<T> list, Function<T, R> function){
		return list.stream().map(function).collect(toList());
	}
	
	private String createAdminUsername(int usernameIndex){
		return ADMIN + usernameIndex;
	}
	
	private String createUserUsername(int usernameIndex) {
		return USER + usernameIndex;
	}
	
	private <T> T extract(CompletableFuture<T> completableFuture) {
		T t = null;
		try {
			t = completableFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error("Error:", e);
		}
		return t;	
	}
}