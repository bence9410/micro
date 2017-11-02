package hu.beni.amusementpark.test;

import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.helper.Statistics;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.service.VisitorService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@TestPropertySource(properties = {"spring.jpa.show-sql=false"})
public class PerformanceTest {
	
	private static final int NUMBER_OF_PARKS = 100;
	private static final int NUMBER_OF_MACHINES_IN_A_PARK = 10;
	private static final int NUMBER_OF_VISITORS = 100;
	private static final int NUMBER_OF_VISITORS_GET_ON_A_MACHINE = NUMBER_OF_VISITORS/NUMBER_OF_MACHINES_IN_A_PARK;

	@Autowired
	private AmusementParkService amusementParkService;

	@Autowired
	private MachineService machineService;

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private GuestBookRegistryService guestBookRegistryService;

	@Autowired
	private MachineRepository machineRepository;

	@Autowired
	private VisitorRepository visitorRepository;
	
	@Autowired
	private Environment environment;

	private Statistics statistics;
	
	@Before
	public void fillDataBaseWithData() {
		statistics  = new Statistics(NUMBER_OF_PARKS, determineDatabaseFromActiveSpringProfiles());
		statistics.start();
		saveAmusementParksWithAddresses();
		statistics.setSaveAmusementParksWithAddresses();

		Page<AmusementPark> page = null;
		Pageable pageable = new PageRequest(0, 10, new Sort("id"));
		statistics.start();
		do {
			page = amusementParkService.findAll(pageable);
			pageable = page.nextPageable();
			page.getContent().stream().forEach(a -> addMachines(a.getId()));
		} while (page.hasNext());
		statistics.setAddMachines();
		
		statistics.start();
		registrateVisitors();
		statistics.setRegistrateVisitors();
	}

	@After
	public void logResults() throws IOException {
		statistics.writeToFile();
	}

	@Test
	public void test() {
		statistics.start();
		List<Long> visitorIds = visitorRepository.findAll().stream().map(v -> v.getId()).collect(Collectors.toList());
		statistics.setReadVisitorIds();
		
		Page<AmusementPark> page = null;
		Pageable pageable = new PageRequest(0, 10, new Sort("id"));
		do {
			page = amusementParkService.findAll(pageable);
			pageable = page.nextPageable();
			page.getContent().stream().map(a -> a.getId()).forEach(amusementParkId -> {
				long startTimeInMillis = statistics.getCurrentTimeMillis();
				List<Long> machineIds = machineRepository.findAllByAmusementParkId(amusementParkId).stream().map(m -> m.getId()).collect(Collectors.toList());
				
				enterPark(amusementParkId, visitorIds);
				getOnMachines(amusementParkId, machineIds, visitorIds);
				getOffMachines(machineIds, visitorIds);
				addRegistriesAndLeavePark(amusementParkId, visitorIds);
				statistics.addOneParkTime(startTimeInMillis);
			});
		} while (page.hasNext());
	}

	private void enterPark(Long amusementParkId, List<Long> visitorIds) {
		statistics.start();
		visitorIds.forEach(visitorId -> visitorService.enterPark(amusementParkId, visitorId, 10000));
		statistics.addEnterParkTime();
	}
	
	private void getOnMachines(Long amusementParkId, List<Long> machineIds, List<Long> visitorIds) {
		statistics.start();
		doubleLoopInnerContinuesWhereFinished(machineIds.size(), NUMBER_OF_VISITORS_GET_ON_A_MACHINE,
				(i, j) -> visitorService.getOnMachine(amusementParkId, machineIds.get(i), visitorIds.get(j)));
		statistics.addGetOnMachineTime();
	}	
	
	private void getOffMachines(List<Long> machineIds, List<Long> visitorIds) {
		statistics.start();
		doubleLoopInnerContinuesWhereFinished(machineIds.size(), NUMBER_OF_VISITORS_GET_ON_A_MACHINE,
				(i, j) -> visitorService.getOffMachine(machineIds.get(i), visitorIds.get(j)));
		statistics.addGetOffMachineTime();
	}
	
	private void addRegistriesAndLeavePark(Long amusementParkId, List<Long> visitorIds) {
		statistics.start();
		visitorIds.forEach(visitorId -> {
			guestBookRegistryService.addRegistry(amusementParkId, visitorId, OPINION_ON_THE_PARK);
			visitorService.leavePark(amusementParkId, visitorId);
		});
		statistics.addAddRegistryAndLeaveParkTime();
	}
	
	private String determineDatabaseFromActiveSpringProfiles() {
		String[] activeSpringProfiles = environment.getActiveProfiles();
		String database = null;
		if(activeSpringProfiles.length == 0) {
			database = "H2 (in-memory)";
		} else {
			database = Stream.of(activeSpringProfiles).filter(p -> "oracleDB".equals(p))
					.findFirst().map(p -> "Oracle").orElse("Unknown");
		}
		return database;
	}
	
	private void saveAmusementParksWithAddresses() {
		IntStream.range(0, NUMBER_OF_PARKS).forEach(i -> amusementParkService.save(createAmusementParkWithAddress()));
	}

	private void addMachines(Long amusementParkId) {
		IntStream.range(0, NUMBER_OF_MACHINES_IN_A_PARK).forEach(i -> machineService.addMachine(amusementParkId, createMachine()));
	}

	private void registrateVisitors() {
		IntStream.range(0, NUMBER_OF_VISITORS).forEach(i -> visitorService.registrate(createVisitor()));
	}
	
	private void doubleLoopInnerContinuesWhereFinished(int outerEndExclusive, int stepInInner, TwoIntConsumer bodyOfInner) {
		IntStream.range(0, outerEndExclusive).forEach(i -> IntStream.range(i * stepInInner,
				i * stepInInner + stepInInner).forEach(j -> bodyOfInner.accept(i, j)));
	}
	
	@FunctionalInterface
	private static interface TwoIntConsumer{
		
		public void accept(int i, int j);
	
	}
}
