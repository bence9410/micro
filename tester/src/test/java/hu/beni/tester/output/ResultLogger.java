package hu.beni.tester.output;

import static hu.beni.tester.constant.Constants.SEMICOLON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import hu.beni.tester.dto.DeleteTime;
import hu.beni.tester.dto.TimeTo;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.properties.NumberOfProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResultLogger {

	private static final File RESULT_FILE = new File("results.csv");
	private static final String NEW_LINE = "\n";
	private static final String GIT_COMMIT_ID;

	static {
		String gitCommitId = "";
		try {
			Properties p = new Properties();
			p.load(ResultLogger.class.getResourceAsStream("/git.properties"));
			gitCommitId = p.getProperty("git.commit.id");
		} catch (Throwable e) {
			log.error("Error: ", e);
		}
		GIT_COMMIT_ID = gitCommitId;
	}

	private final String[] header = { "", "fullRun", "createAmusementParksWithMachines",
			"findAllParksPagedBeforeVisitorStuff", "wholeVisitorStuff", "tenParkVisitorStuff", "oneParkVisitorStuff",
			"findAllParksPagedAfterVisitorStuff", "findAllVisitorsPaged", "wholeDeleteParks", "tenDeleteParks",
			"gitCommitId" };

	private final String[] result;

	public ResultLogger(TimeTo timeTo, ApplicationProperties properties) {
		NumberOfProperties numberOf = properties.getNumberOf();
		DeleteTime deleteParks = timeTo.getDeleteParks();
		result = new String[] {
				numberOf.getAdmins() + "a " + numberOf.getVisitors() + "v " + numberOf.getAmusementParksPerAdmin()
						+ "p/a " + numberOf.getMachinesPerPark() + "m/p ",
				Long.toString(timeTo.getFullRun()), minAvgMax(timeTo.getCreateAmusementParksWithMachines()),
				minAvgMax(timeTo.getFindAllParksPagedBeforeVisitorStuff()), minAvgMax(timeTo.getWholeVisitorStuff()),
				minAvgMax(timeTo.getTenParkVisitorStuff()), minAvgMax(timeTo.getOneParkVisitorStuff()),
				minAvgMax(timeTo.getFindAllParksPagedAfterVisitorStuff()), minAvgMax(timeTo.getFindAllVisitorsPaged()),
				Long.toString(deleteParks.getWholeTime()), minAvgMax(deleteParks.getTenDeleteTimes()), GIT_COMMIT_ID };
	}

	public void logToConsole() {
		StringBuilder sb = new StringBuilder(NEW_LINE);
		for (int i = 0; i < header.length; i++) {
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
		try (PrintWriter printWriter = new PrintWriter(new FileOutputStream(RESULT_FILE, true))) {
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
