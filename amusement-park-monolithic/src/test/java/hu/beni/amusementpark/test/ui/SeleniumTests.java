package hu.beni.amusementpark.test.ui;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.ERROR;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.UNEXPECTED_ERROR_OCCURED;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import hu.beni.amusementpark.helper.DriverFacade;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SeleniumTests {

	private static final FirefoxOptions FIREFOX_OPTIONS;

	static {
		System.setProperty("webdriver.gecko.driver", "/opt/geckodriver");
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		FIREFOX_OPTIONS = new FirefoxOptions();
		FIREFOX_OPTIONS.setBinary(firefoxBinary);
	}

	private final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

	private final List<String> errors = new ArrayList<>();

	@LocalServerPort
	private int port;

	@Test
	public void test() {
		for (File testFile : new File("./src/test/resources/js/selenium/").listFiles()) {
			DriverFacade driverFacade = new DriverFacade(new FirefoxDriver(FIREFOX_OPTIONS));
			runTestFile(driverFacade, testFile);
			driverFacade.quit();
		}
		errors.stream().map(this::addNewLineToBeginning).reduce(String::concat).ifPresent(this::throwRuntimeException);
	}

	private void runTestFile(DriverFacade driverFacade, File testFile) {
		String errorMessage = null;
		String testFileName = testFile.getName();
		try {
			driverFacade.get("http://localhost:" + port);
			scriptEngine.put("driver", driverFacade);
			scriptEngine.eval(new FileReader(testFile));
		} catch (TimeoutException e) {
			errorMessage = testFileName + ": " + getSeleniumErrorMessage(e);
			takeScreenshot(driverFacade, testFileName);
		} catch (Throwable t) {
			log.error(ERROR, t);
			errorMessage = UNEXPECTED_ERROR_OCCURED;
			takeScreenshot(driverFacade, testFileName);
		}
		Optional.ofNullable(errorMessage).ifPresent(errors::add);
	}

	private String getSeleniumErrorMessage(TimeoutException e) {
		String seleniumErrorMessage;
		try {
			Field detailMessage = Throwable.class.getDeclaredField("detailMessage");
			detailMessage.setAccessible(true);
			seleniumErrorMessage = String.class.cast(detailMessage.get(e));
		} catch (Throwable t) {
			seleniumErrorMessage = e.getMessage();
		}
		return seleniumErrorMessage;
	}

	private void takeScreenshot(DriverFacade driverFacade, String testFileName) {
		try {
			File outDir = new File("./target/selenium-screenshots/");
			outDir.mkdirs();
			FileCopyUtils.copy(driverFacade.takeScreenshot(),
					new File(outDir, testFileName + LocalDateTime.now() + ".png"));
		} catch (Throwable t) {
			log.error("Could not take screenshot for " + testFileName, t);
		}
	}

	private String addNewLineToBeginning(String string) {
		return "\n" + string;
	}

	private void throwRuntimeException(String message) {
		throw new RuntimeException(message);
	}

}
