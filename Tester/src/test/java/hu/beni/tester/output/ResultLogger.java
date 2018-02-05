package hu.beni.tester.output;

import static hu.beni.tester.AmusementParkTesterApplicationTests.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import hu.beni.tester.dto.DeleteTime;
import hu.beni.tester.dto.TimeTo;
import lombok.extern.slf4j.Slf4j;

import static hu.beni.tester.constant.Constants.*;

@Slf4j
public class ResultLogger {
	
	private static final File RESULT_FILE = new File("results.csv");
	private static final String NEW_LINE = "\n";
	private static final String SEMICOLON = ";";
	
	private final String[] header = { "",
			"createAmusementParksWithMachines", 
			"findAllParksPagedBeforeVisitorStuff",
			"wholeVisitorStuff",
			"oneParkVisitorStuff", 
			"findAllParksPagedAfterVisitorStuff",
			"findAllVisitorsPaged", 
			"wholeDeleteParks", 
			"tenDeleteParks",
			"wholeDeleteVisitors",
			"tenDeleteVisitors" };
	
	private final String[] result;

	public ResultLogger(TimeTo timeTo) {
		DeleteTime deleteParks = timeTo.getDeleteParks();
		DeleteTime deleteVisitors = timeTo.getDeleteVisitors();
		result = new String[] { NUMBER_OF_ADMINS + "a " + NUMBER_OF_USERS + "v " + NUMBER_OF_PARKS_TO_CREATE_PER_ADMIN  + "p/a " + NUMBER_OF_MACHINES_TO_CREATE_FOR_EACH_PARK + "m/p ",
				minAvgMax(timeTo.getCreateAmusementParksWithMachines()),
				minAvgMax(timeTo.getFindAllParksPagedBeforeVisitorStuff()), 
				minAvgMax(timeTo.getWholeVisitorStuff()),
				minAvgMax(timeTo.getOneParkVisitorStuff()), 
				minAvgMax(timeTo.getFindAllParksPagedAfterVisitorStuff()),
				minAvgMax(timeTo.getFindAllVisitorsPaged()), 
				Long.toString(deleteParks.getWholeTime()),
				minAvgMax(deleteParks.getTenDeleteTimes()),
				Long.toString(deleteVisitors.getWholeTime()),
				minAvgMax(deleteVisitors.getTenDeleteTimes()) };	
	}

	public void logToConsole() {
		StringBuilder sb = new StringBuilder(NEW_LINE);
		for(int i = 0; i < header.length; i++) {
			if (i != 0) {
				sb.append(header[i]).append(": ");
			}
			sb.append(result[i]);
			if (i != header.length - 1) {
				sb.append(NEW_LINE);
			}
		}
		log.info(sb.toString());
	}
	
	public void writeToFile() {
		boolean firstWrite = !RESULT_FILE.exists();
		try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(RESULT_FILE, true))){
			if (firstWrite) {
				printWriter.println(String.join(SEMICOLON, header));
			}
			printWriter.println(String.join(SEMICOLON, result));	
		} catch (FileNotFoundException e) {
			log.error("Could not write results to file", e);
		}
	}
	
	private String minAvgMax(List<Long> list) {
		long first = list.get(0);
		int size = list.size();
		long min = first;
		long avg = first;
		long max = first;
		for (int i = 1; i < list.size(); i++) {
			long act = list.get(i);
			if (act < min) {
				min = act;
			}
			if (act > max) {
				max = act;
			}
			avg += act;
		}
		avg /= size;
		return min + ", " + avg + ", " + max;
	}
}
