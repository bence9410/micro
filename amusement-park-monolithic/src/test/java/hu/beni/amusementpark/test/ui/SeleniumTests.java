package hu.beni.amusementpark.test.ui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.entity.Address;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.helper.ValidEntityFactory;

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

	private DriverFacade driverFacade;

	@LocalServerPort
	private int port;

	@Before
	public void setUp() {

		driverFacade = new DriverFacade(new FirefoxDriver(FIREFOX_OPTIONS));
		driverFacade.get("http://localhost:" + port);
	}

	@After
	public void tearDown() {
		driverFacade.quit();
	}

	@Test
	public void createAmusementParkPositiveTest() {
		login("admin", "password");

		createAmusementParkAndCheckResult(ValidEntityFactory.createAmusementParkWithAddress());
	}

	@Test
	public void signUpAndUploadMoneyPositiveTest() {
		driverFacade.click("#goToSignUp");

		driverFacade.write("#signUpUsername", "visitor");
		driverFacade.write("#signUpPassword", "password");
		driverFacade.write("#signUpConfirmPassword", "password");
		driverFacade.write("#name", "Visitor Lajos");
		driverFacade.write("#dateOfBirth", "1994-10-10");
		driverFacade.click("#signUp");

		driverFacade.text("#username", "visitor");
		driverFacade.text("#spendingMoney", "250");

		driverFacade.click("#uploadMoney");
		driverFacade.write("#money", "100");
		driverFacade.click("#upload");
		driverFacade.text("#moneyUploadResult", "success");
		driverFacade.click("#closeUpload");
		driverFacade.hidden("#money");

		driverFacade.text("#spendingMoney", "350");
	}

	private void login(String username, String password) {
		driverFacade.write("#loginUsername", username);
		driverFacade.write("#password", password);
		driverFacade.click("#login");

		driverFacade.text("#username", username);
	}

	private void createAmusementParkAndCheckResult(AmusementPark amusementPark) {
		Address address = amusementPark.getAddress();
		driverFacade.write("#parkName", amusementPark.getName());
		driverFacade.write("#capital", amusementPark.getCapital().toString());
		driverFacade.write("#totalArea", amusementPark.getTotalArea().toString());
		driverFacade.write("#entranceFee", amusementPark.getEntranceFee().toString());

		driverFacade.write("#country", address.getCountry());
		driverFacade.write("#zipCode", address.getZipCode());
		driverFacade.write("#city", address.getCity());
		driverFacade.write("#street", address.getStreet());
		driverFacade.write("#houseNumber", address.getHouseNumber());

		driverFacade.click("#save");

		driverFacade.text("#result", "success");

		driverFacade.text("#tableBody>tr>td:nth-child(1)", amusementPark.getName());
		driverFacade.text("#tableBody>tr>td:nth-child(2)", amusementPark.getCapital().toString());
		driverFacade.text("#tableBody>tr>td:nth-child(3)", amusementPark.getTotalArea().toString());
		driverFacade.text("#tableBody>tr>td:nth-child(4)", amusementPark.getEntranceFee().toString());
		driverFacade.text("#tableBody>tr>td:nth-child(5)", address.getCountry() + " " + address.getZipCode() + " "
				+ address.getCity() + " " + address.getStreet() + " " + address.getHouseNumber());
		driverFacade.visible("#tableBody>tr>td:nth-child(6)>input[value='Delete']");
	}

}
