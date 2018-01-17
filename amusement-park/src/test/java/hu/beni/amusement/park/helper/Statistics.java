package hu.beni.amusement.park.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringJoiner;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Statistics {
	
	private static final String GIT_BRANCH;
	private static final String GIT_COMMIT_ID;
	private static final String GIT_COMMIT_MESSAGE;
	private static final File RESULT_DIR;

	static {
		String gitBranch = null;
		String gitCommitId = null;
		String gitCommitMessage = null;
		try {
			Properties p = new Properties();
			p.load(Statistics.class.getResourceAsStream("/git.properties"));
			gitBranch = p.getProperty("git.branch");
			gitCommitId = p.getProperty("git.commit.id");
			gitCommitMessage = p.getProperty("git.commit.message.full");
		} catch (IOException e) {
			log.error("Error: ", e);
		}
		GIT_BRANCH = gitBranch;
		GIT_COMMIT_ID = gitCommitId;
		GIT_COMMIT_MESSAGE = gitCommitMessage;
		RESULT_DIR = new File("performanceTestResults/");
		RESULT_DIR.mkdir();
	}
	
	private static String joinByDelimiter(String delimiter, String... strings) {
		StringJoiner joiner = new StringJoiner(delimiter);
		Stream.of(strings).forEach(s -> joiner.add(s));
		return joiner.toString();
	}
	
	private final String database;
	
	private long saveAmusementParksWithAddresses;
	private long addMachines;
	private long registrateVisitors;
	private long readVisitorIds;
	
	private List<Long> enterPark;
	private List<Long> getOnMachines;
	private List<Long> getOffMachines;	
	private List<Long> addRegistryAndLeavePark;
	private List<Long> wholeAmusementPark;
	
	private long counter;
	
	public Statistics(int numberOfParks, String database) {
		this.database = database;
		enterPark = new ArrayList<>(numberOfParks);
		getOnMachines = new ArrayList<>(numberOfParks);
		getOffMachines = new ArrayList<>(numberOfParks);	
		addRegistryAndLeavePark = new ArrayList<>(numberOfParks);
		wholeAmusementPark = new ArrayList<>(numberOfParks);
	}
	
	public long getCurrentTimeMillis() {
		return System.currentTimeMillis();
	}
	
	public void start() {
		counter = getCurrentTimeMillis();
	}
		
	public void setSaveAmusementParksWithAddresses() {
		saveAmusementParksWithAddresses = end();
	}

	public void setAddMachines() {
		addMachines = end();
	}

	public void setRegistrateVisitors() {
		registrateVisitors = end();
	}
	
	public void setReadVisitorIds() {
		readVisitorIds = end();
	}
	
	public void addEnterParkTime() {
		enterPark.add(end());
	}
	
	public void addGetOnMachineTime() {
		getOnMachines.add(end());
	}
	
	public void addGetOffMachineTime() {
		getOffMachines.add(end());
	}

	public void addAddRegistryAndLeaveParkTime() {
		addRegistryAndLeavePark.add(end());
	}
	
	public void addOneParkTime(long startTimeInMillis) {
		wholeAmusementPark.add(calculateTime(startTimeInMillis));
	}
	
	public void writeToFile() {
		File resultFile = new File(RESULT_DIR, database+".csv");
		boolean firstWrite = !resultFile.exists();
		try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(resultFile, true))){
			if (firstWrite) {
				printWriter.println(getHeader());
			}
			printWriter.println(getResults());	
		} catch (FileNotFoundException e) {
			log.error("Could not write results to file", e);
		}
	}
	
	private long end() {
		return calculateTime(counter);
	}
	
	private long calculateTime(long startTimeInMillis) {
		return getCurrentTimeMillis() - startTimeInMillis;
	}
	
	private String getHeader() {
		return joinByDelimiter(";", "Save Amusement Parks", "Add Machines", "Registrate Visitors",
				"Read Visitor Ids", "Enter Park", "Get on Machines", "Get off Machines", 
				"Add Registry and Leave Park", "Sum for 1 Park", "git branch", "commit id", "commit message");
	}
	
	private String getResults() {
		return joinByDelimiter(";", Long.toString(saveAmusementParksWithAddresses), Long.toString(addMachines), 
				Long.toString(registrateVisitors), Long.toString(readVisitorIds), getMinAsString(enterPark),
				getMinAsString(getOnMachines), getMinAsString(getOffMachines), getMinAsString(addRegistryAndLeavePark), 
				getMinAsString(wholeAmusementPark), GIT_BRANCH, GIT_COMMIT_ID, GIT_COMMIT_MESSAGE);
	}
	
	private String getMinAsString(List<Long> data) {
		long min = data.get(0);
		
		for(int i = 1; i < data.size(); i++) {
			long actual = data.get(i);
			if (min > actual) {
				min = actual;
			}
		}
		return Long.toString(min);
	}
	
}