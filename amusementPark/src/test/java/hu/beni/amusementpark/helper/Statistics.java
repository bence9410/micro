package hu.beni.amusementpark.helper;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Statistics {
	
	private long millisToSaveOneHundredAmusementParkWithAddress;
	private long millisToAddOneThousandMachine;
	private long millisToRegistrateOneHundredVisitor;
	private long millisToGetOneHundredVisitorId;
	private long totalTestRunningTime;
	
	private List<Long> millisForOneHundredVisitorsToEnter = new ArrayList<>(100);
	private List<Long> millisForOneHundredVisitorToGetOnTenDifferentMachines = new ArrayList<>(100);
	private List<Long> millisForOneHundredVisitorsToGetOffTenDifferentMachines = new ArrayList<>(100);	
	private List<Long> millisForOneHundredVisitorsToAddRegistryAndLeave = new ArrayList<>(100);
	private List<Long> millisForOneAmusementPark = new ArrayList<>(100);
	private List<Long> millisForTenAmusementPark = new ArrayList<>(10);

	private long counter;
	
	public long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}
	
	public void start() {
		counter = getCurrentTimeMillis();
	}
		
	public void setMillisToSaveOneHundredAmusementParkWithAddress() {
		millisToSaveOneHundredAmusementParkWithAddress = end();
	}

	public void setMillisToAddOneThousandMachine() {
		millisToAddOneThousandMachine = end();
	}

	public void setMillisToRegistrateOneHundredVisitor() {
		millisToRegistrateOneHundredVisitor = end();
	}
	
	public void setMillisToGetOneHundredVisitorId() {
		millisToGetOneHundredVisitorId = end();
	}
	
	public void setTotalTestRunningTime(long testStartTime) {
		this.totalTestRunningTime = calculateTime(testStartTime);
	}
	
	public void addEnterTime() {
		millisForOneHundredVisitorsToEnter.add(end());
	}
	
	public void addGetOnTime() {
		millisForOneHundredVisitorToGetOnTenDifferentMachines.add(end());
	}
	
	public void addGetOffTime() {
		millisForOneHundredVisitorsToGetOffTenDifferentMachines.add(end());
	}

	public void addRegistryAndLeaveTime() {
		millisForOneHundredVisitorsToAddRegistryAndLeave.add(end());
	}
	
	public void addOneParkTime(long startTimeInMillis) {
		millisForOneAmusementPark.add(calculateTime(startTimeInMillis));
	}
	
	public void addTenParkTime(long startTimeInMillis) {
		millisForTenAmusementPark.add(calculateTime(startTimeInMillis));
	}
	
	public void logResults() {
		log.info("millisToSaveOneHundredAmusementParkWithAddress: " + millisToSaveOneHundredAmusementParkWithAddress);
		log.info("millisToAddOneThousandMachine: " + millisToAddOneThousandMachine);
		log.info("millisToRegistrateOneHundredVisitor: " + millisToRegistrateOneHundredVisitor);
		log.info("millisToGetOneHundredVisitorId: " + millisToGetOneHundredVisitorId);
		log.info("millisForOneHundredVisitorsToEnter: " + calculateMinMaxAvgAndGetAsNiceString(millisForOneHundredVisitorsToEnter));
		log.info("millisForOneHundredVisitorToGetOnTenDifferentMachines: " + calculateMinMaxAvgAndGetAsNiceString(millisForOneHundredVisitorToGetOnTenDifferentMachines));
		log.info("millisForOneHundredVisitorsToGetOffTenDifferentMachines: " + calculateMinMaxAvgAndGetAsNiceString(millisForOneHundredVisitorsToGetOffTenDifferentMachines));
		log.info("millisForOneAmusementPark: " + calculateMinMaxAvgAndGetAsNiceString(millisForOneAmusementPark));
		log.info("millisForTenAmusementPark: " + calculateMinMaxAvgAndGetAsNiceString(millisForTenAmusementPark));
		log.info("totalRunningTime: " + totalTestRunningTime);
	}
	
	public long end() {
		return calculateTime(counter);
	}
	
	private long calculateTime(long startTimeInMillis) {
		return getCurrentTimeMillis() - startTimeInMillis;
	}

	private String calculateMinMaxAvgAndGetAsNiceString(List<Long> data) {
		long avg = data.get(0);
		long min = avg;
		long max = avg;
		
		for(int i = 1; i < data.size(); i++) {
			long actual = data.get(i);
			if (min > actual) {
				min = actual;
			}
			if (max < actual) {
				max = actual;
			}
			avg += actual;
		}
		return new StringBuilder("Minimum: ").append(min).append(", Maximum: ")
				.append(max).append(", Avarage: ").append(avg / data.size()).append('.').toString();
	
	}	
	
}
