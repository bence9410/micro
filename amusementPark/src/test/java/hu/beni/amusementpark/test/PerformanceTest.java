package hu.beni.amusementpark.test;

import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementParkWithAddress;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createMachine;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
		statistics  = new Statistics(environment.getActiveProfiles());
		statistics.start();
		saveOneHundredAmusementPark();
		statistics.setMillisToSaveOneHundredAmusementParkWithAddress();

		Page<AmusementPark> page = null;
		Pageable pageable = new PageRequest(0, 10, new Sort("id"));
		statistics.start();
		do {
			page = amusementParkService.findAll(pageable);
			pageable = page.nextPageable();
			page.getContent().stream().forEach(a -> saveTenMachineToAmusementPark(a.getId()));
		} while (page.hasNext());
		statistics.setMillisToAddOneThousandMachine();
		
		statistics.start();
		registrateOneHundredVisitor();
		statistics.setMillisToRegistrateOneHundredVisitor();
	}

	@After
	public void logResults() throws IOException {
		statistics.logResults();	
	}

	@Test
	public void test() {
		long testStartTime = statistics.getCurrentTimeMillis();
		statistics.start();
		List<Long> visitorIds = visitorRepository.findAll().stream().map(v -> v.getId()).collect(Collectors.toList());
		statistics.setMillisToGetOneHundredVisitorId();
		
		Page<AmusementPark> page = null;
		Pageable pageable = new PageRequest(0, 10, new Sort("id"));
		do {
			page = amusementParkService.findAll(pageable);
			pageable = page.nextPageable();
			long startTimeInMillisTenPark = statistics.getCurrentTimeMillis();
			page.getContent().stream().map(a -> a.getId()).forEach(amusementParkId -> {
				long startTimeInMillis = statistics.getCurrentTimeMillis();
				List<Long> machineIds = machineRepository.findAllByAmusementParkId(amusementParkId).stream().map(m -> m.getId()).collect(Collectors.toList());
				
				enterPark(amusementParkId, visitorIds);
				getOnMachines(amusementParkId, machineIds, visitorIds);
				getOffMachines(machineIds, visitorIds);
				addRegistriesAndLeavePark(amusementParkId, visitorIds);
				statistics.addOneParkTime(startTimeInMillis);
			});
			statistics.addTenParkTime(startTimeInMillisTenPark);
		} while (page.hasNext());
		statistics.setTotalTestRunningTime(testStartTime);
	}

	private void enterPark(Long amusementParkId, List<Long> visitorIds) {
		statistics.start();
		visitorIds.forEach(visitorId -> visitorService.enterPark(amusementParkId, visitorId, 10000));
		statistics.addEnterTime();
	}
	
	private void getOnMachines(Long amusementParkId, List<Long> machineIds, List<Long> visitorIds) {
		statistics.start();
		for (int i = 0; i < machineIds.size(); i++) {
			for (int j = i * 10; j < i * 10 + 10; j++) {
				visitorService.getOnMachine(amusementParkId, machineIds.get(i), visitorIds.get(j));
			}
		}
		statistics.addGetOnTime();
	}
	
	private void getOffMachines(List<Long> machineIds, List<Long> visitorIds) {
		statistics.start();
		for (int i = 0; i < machineIds.size(); i++) {
			for (int j = i * 10; j < i * 10 + 10; j++) {
				visitorService.getOffMachine(machineIds.get(i), visitorIds.get(j));
			}
		}
		statistics.addGetOffTime();
	}
	
	private void addRegistriesAndLeavePark(Long amusementParkId, List<Long> visitorIds) {
		statistics.start();
		visitorIds.forEach(visitorId -> {
			guestBookRegistryService.addRegistry(amusementParkId, visitorId, OPINION_ON_THE_PARK);
			visitorService.leavePark(amusementParkId, visitorId);
		});
		statistics.addRegistryAndLeaveTime();
	}
	
	private void saveOneHundredAmusementPark() {
		IntStream.range(0, 100).forEach(i -> amusementParkService.save(createAmusementParkWithAddress()));
	}

	private void saveTenMachineToAmusementPark(Long amusementParkId) {
		IntStream.range(0, 10).forEach(i -> machineService.addMachine(amusementParkId, createMachine()));
	}

	private void registrateOneHundredVisitor() {
		IntStream.range(0, 100).forEach(i -> visitorService.registrate(createVisitor()));
	}

}
