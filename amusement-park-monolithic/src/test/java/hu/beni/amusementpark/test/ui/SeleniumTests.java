package hu.beni.amusementpark.test.ui;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.helper.DriverFacade;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SeleniumTests {

	static {
		System.setProperty("webdriver.gecko.driver", "/home/bence/Downloads/geckodriver");
	}

	@LocalServerPort
	private int port;

	private DriverFacade driverFacade;

	@Rule
	public TestWatcher watcher = new TestWatcher() {
		@Override
		protected void failed(Throwable e, Description description) {
			try {
				File f = new File("/home/bence/Documents/kepek/" + description.getMethodName()
						+ LocalDateTime.now().toString().replace(':', '-') + ".png");
				File a = driverFacade.takeScreenshot();

				FileCopyUtils.copy(a, f);
				driverFacade.quit();
			} catch (IOException e1) {
				throw new RuntimeException(e1);

			}

		}

		@Override
		protected void succeeded(Description description) {
			driverFacade.quit();
		}
	};

	@Before
	public void setUp() {
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		driverFacade = new DriverFacade(new FirefoxDriver(firefoxOptions));
	}

	@Test
	public void signUp() {
		driverFacade.get("http://localhost:" + port);
		driverFacade.click("#showSignUpButton");
		driverFacade.write("#signUpLoginEmail", "jeni@gmail.com");
		driverFacade.write("#signUpPassword", "password");
		driverFacade.write("#signUpConfirmPassword", "password");
		driverFacade.write("#dateOfBirth", "1995-03-01");
		driverFacade.setAttribute("img", "src", "data:image/jpeg;base64,kep");
		driverFacade.click("#signUpButton");
		driverFacade.text("#email", "jeni@gmail.com");
		driverFacade.click("#amusementParkShowSearchButton");
		searchAmusementPark("Bence parkja", 20000, 28800, 2000, 2000, 50, 50);
		driverFacade.numberOfRowsInTable("#tableBody", 1);

	}

	@Test
	public void testAmusementParkCreate() {
		driverFacade.get("http://localhost:" + port);
		driverFacade.write("#loginEmail", "bence@gmail.com");
		driverFacade.write("#password", "password");
		driverFacade.click("#login");
		driverFacade.text("#email", "bence@gmail.com");
		driverFacade.click("#uploadMoney");
		int actMoney = Integer.parseInt(driverFacade.visible("#spendingMoney").getText()) + 2000;
		driverFacade.write("#money", "2000");
		driverFacade.click("#upload");
		driverFacade.click("#closeUpload");
		driverFacade.hidden(".modal-backdrop");
		driverFacade.text("#spendingMoney", Integer.toString(actMoney));
		createAmusementParks("Mária parkja", 500, 1000, 200);
		createAmusementParks("Ferenc parkja", 600, 1000, 200);
		createAmusementParks("Beni parkja", 1000, 150, 50);
		createAmusementParks("Ilona parkja", 12000, 100, 150);
		createAmusementParks("Anita parkja", 14000, 1200, 180);
		createAmusementParks("János parkja", 1900, 2000, 150);
		createAmusementParks("Anna parkja", 15000, 1100, 180);
		createAmusementParks("Bálint parkja", 18000, 1200, 190);
		createAmusementParks("Erzsébet parkja", 12000, 1300, 200);
		createAmusementParks("Fanni parkja", 17000, 1100, 50);
		createAmusementParks("Anita parkja", 16000, 1500, 90);
		createAmusementParks("Viktor parkja", 14000, 1000, 80);
		createAmusementParks("Elemér parkja", 13000, 1400, 70);
		createAmusementParks("Csaba parkja", 12000, 1500, 120);
		createAmusementParks("Misi parkja", 11000, 1300, 190);
		createAmusementParks("Dominika parkja", 9000, 1200, 180);
		createAmusementParks("Ráhel parkja", 8000, 1100, 170);
		createAmusementParks("Dani parkja", 7000, 1250, 160);
		createAmusementParks("Balázs parkja", 6000, 1150, 150);
		createAmusementParks("Éva parkja", 1000, 150, 50);
		createAmusementParks("Magdolna parkja", 12000, 100, 150);
		createAmusementParks("Zsolt parkja", 11000, 1200, 180);
		createAmusementParks("János parkja", 1900, 500, 150);
		createAmusementParks("Anna parkja", 15000, 1100, 180);
		createAmusementParks("Bálint parkja", 18000, 1200, 190);
		createAmusementParks("Erzsébet parkja", 12000, 1300, 200);
		createAmusementParks("Fanni parkja", 17000, 1100, 50);
		createAmusementParks("Pétel parkja", 16000, 1500, 90);
		createAmusementParks("Pál parkja", 14000, 1000, 80);
		driverFacade.click("#last");
		driverFacade.text("#numberOfPage", "3/3");
		driverFacade.click("#first");
		driverFacade.text("#numberOfPage", "1/3");
		driverFacade.click("#right");
		driverFacade.text("#numberOfPage", "2/3");
		driverFacade.click("#right");
		driverFacade.text("#numberOfPage", "3/3");
		driverFacade.click("#left");
		driverFacade.text("#numberOfPage", "2/3");
		driverFacade.click("#amusementParkShowSearchButton");
		searchAmusementPark("Anita parkja", 1000, 20000, 150, 1500, 50, 200);
		searchAmusementPark("park", 1000, 20000, 150, 1500, 50, 200);
		driverFacade.text("#numberOfPage", "1/3");
		searchAmusementPark("park", 1000, 10000, 150, 1500, 50, 100);
		driverFacade.numberOfRowsInTable("#tableBody", 2);
		searchAmusementPark("park", 1000, 5000, 10, 1000, 50, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 3);
		searchAmusementPark("park", 500, 1000, 10, 1000, 50, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 4);
		searchAmusementPark("e", 1000, 20000, 10, 800, 50, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 1);
		searchAmusementPark("parks", 4000, 20000, 10, 500, 50, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 0);
		searchAmusementPark("park", 1000, 20000, 100, 300, 50, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 4);
		searchAmusementPark("a", 500, 12000, 1000, 2000, 50, 200);
		driverFacade.text("#numberOfPage", "1/2");
		searchAmusementPark("p", 1000, 15000, 1000, 2000, 50, 150);
		driverFacade.numberOfRowsInTable("#tableBody", 6);
		searchAmusementPark("k", 10000, 20000, 1000, 2000, 100, 200);
		driverFacade.text("#numberOfPage", "1/1");
		searchAmusementPark("i", 10000, 20000, 1000, 2000, 100, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 4);
		searchAmusementPark("park", 2000, 10000, 1200, 2000, 150, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 2);
		searchAmusementPark("park", 1000, 8000, 500, 2000, 100, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 5);
		searchAmusementPark("park", 2000, 20000, 1000, 2000, 100, 150);
		driverFacade.numberOfRowsInTable("#tableBody", 2);
		searchAmusementPark("park", 2000, 20000, 1500, 2000, 100, 150);
		driverFacade.numberOfRowsInTable("#tableBody", 1);
		searchAmusementPark("r", 1000, 20000, 10, 500, 150, 200);
		driverFacade.numberOfRowsInTable("#tableBody", 3);
		searchAmusementPark("á", 1400, 10000, 1400, 2000, 60, 150);
		driverFacade.numberOfRowsInTable("#tableBody", 1);
	}

	@Test
	public void guestBookSearch() {
		driverFacade.get("http://localhost:" + port);
		driverFacade.write("#loginEmail", "bence@gmail.com");
		driverFacade.write("#password", "password");
		driverFacade.click("#login");
		driverFacade.text("#email", "bence@gmail.com");
		driverFacade.click("#uploadMoney");
		int actMoney = Integer.parseInt(driverFacade.visible("#spendingMoney").getText()) + 2000;
		driverFacade.write("#money", "2000");
		driverFacade.click("#upload");
		driverFacade.click("#closeUpload");
		driverFacade.hidden(".modal-backdrop");
		driverFacade.text("#spendingMoney", Integer.toString(actMoney));
		driverFacade.click("#tableBody td");
		driverFacade.click("#enterPark");
		guestBookWrite("Nagyon jó volt.");
		guestBookWrite("Nagyon tetszett.");
		guestBookWrite("Élveztem.");
		guestBookWrite("Jó volt.");
		LocalDateTime min = LocalDateTime.now();
		guestBookWrite("Tetszett.");
		guestBookWrite("Izgalmas volt.");
		guestBookWrite("Tetszett, máskor is jövök.");
		guestBookWrite("Fantasztikus volt.");
		guestBookWrite("Érdekes volt.");
		guestBookWrite("Szép volt.");
		guestBookWrite("Elment.");
		guestBookWrite("Tetszett.");
		LocalDateTime cent = LocalDateTime.now();
		guestBookWrite("Jó volt.");
		guestBookWrite("Nem tetszett annyira.");
		guestBookWrite("Élveztem.");
		guestBookWrite("Izgalmas volt.");
		LocalDateTime max = LocalDateTime.now();
		guestBookWrite("Nagyon jó volt.");
		guestBookWrite("Nagyon tetszett.");
		guestBookWrite("Jó");
		guestBookWrite("Izgalmas volt.");
		LocalDateTime max2 = LocalDateTime.now();
		guestBookWrite("Tetszett, máskor is jövök.");
		guestBookWrite("Fantasztikus volt.");
		guestBookWrite("Érdekes volt.");
		guestBookWrite("Szép volt.");
		guestBookWrite("Elment.");
		guestBookWrite("Tetszett.");
		driverFacade.click("#guestBookButton");
		guestBookSearch(min.toString(), cent.toString(), "", "");
		driverFacade.numberOfRowsInTable("#guestBookTable", 8);
		driverFacade.text("#guestBookNumberOfPage", "1/1");
		guestBookSearch(min.toString(), max.toString(), "", "");
		driverFacade.numberOfRowsInTable("#guestBookTable", 10);
		guestBookSearch(min.toString(), max.toString(), "jeni", "");
		driverFacade.numberOfRowsInTable("#guestBookTable", 0);
		driverFacade.text("#guestBookNumberOfPage", "0/0");
		guestBookSearch(min.toString(), max.toString(), "", "o");
		driverFacade.numberOfRowsInTable("#guestBookTable", 7);
		guestBookSearch(min.toString(), max2.toString(), "", "");
		driverFacade.text("#guestBookNumberOfPage", "1/2");
		driverFacade.click("#guestBookRight");
		driverFacade.text("#guestBookNumberOfPage", "2/2");
		driverFacade.click("#guestBookFirst");
		driverFacade.text("#guestBookNumberOfPage", "1/2");
		driverFacade.click("#guestBookLast");
		driverFacade.text("#guestBookNumberOfPage", "2/2");
		driverFacade.click("#guestBookLeft");
		driverFacade.text("#guestBookNumberOfPage", "1/2");
		driverFacade.click("#guestBookModal button");
		driverFacade.hidden(".modal-backdrop");
		driverFacade.click("#headerButton input:nth-child(4)");
		driverFacade.click("#tableBody td");
		driverFacade.click("#guestBookLast");
		driverFacade.text("#guestBookNumberOfPage", "3/3");
		driverFacade.numberOfRowsInTable("#guestBookTable", 8);
		driverFacade.click("#guestBookFirst");
		driverFacade.text("#guestBookNumberOfPage", "1/3");
		driverFacade.click("#guestBookRight");
		driverFacade.text("#guestBookNumberOfPage", "2/3");
		driverFacade.click("#guestBookLeft");
		driverFacade.text("#guestBookNumberOfPage", "1/3");
		guestBookSearch(min.toString(), cent.toString(), "", "");
		driverFacade.numberOfRowsInTable("#guestBookTable", 8);
		driverFacade.text("#guestBookNumberOfPage", "1/1");
		guestBookSearch(min.toString(), max.toString(), "", "");
		driverFacade.numberOfRowsInTable("#guestBookTable", 10);
		driverFacade.text("#guestBookNumberOfPage", "1/2");
		guestBookSearch(min.toString(), max.toString(), "jeni", "");
		driverFacade.numberOfRowsInTable("#guestBookTable", 0);
		driverFacade.text("#guestBookNumberOfPage", "0/0");
		guestBookSearch(min.toString(), max.toString(), "", "o");
		driverFacade.numberOfRowsInTable("#guestBookTable", 7);
		guestBookSearch(min.toString(), max2.toString(), "", "");
		driverFacade.click("#guestBookRight");
		driverFacade.text("#guestBookNumberOfPage", "2/2");
		driverFacade.click("#guestBookLeft");
		driverFacade.text("#guestBookNumberOfPage", "1/2");
		driverFacade.click("#guestBookLast");
		driverFacade.text("#guestBookNumberOfPage", "2/2");
		driverFacade.click("#guestBookFirst");
		driverFacade.text("#guestBookNumberOfPage", "1/2");
		driverFacade.click("#amusementParkDetails button");

	}

	@Test
	public void getOnMachineTest() {
		driverFacade.get("http://localhost:" + port);
		driverFacade.write("#loginEmail", "bence@gmail.com");
		driverFacade.write("#password", "password");
		driverFacade.click("#login");
		driverFacade.text("#email", "bence@gmail.com");
		driverFacade.click("#uploadMoney");
		int actMoney = Integer.parseInt(driverFacade.visible("#spendingMoney").getText()) + 2000;
		driverFacade.write("#money", "2000");
		driverFacade.click("#upload");
		driverFacade.click("#closeUpload");
		driverFacade.hidden(".modal-backdrop");
		driverFacade.text("#spendingMoney", Integer.toString(actMoney));
		driverFacade.click("#amusementParkShowSearchButton");
		searchAmusementPark("Bence parkja", 20000, 28800, 2000, 2000, 50, 50);
		driverFacade.numberOfRowsInTable("#tableBody", 1);
		driverFacade.click("#tableBody td");
		driverFacade.click("#enterPark");
		getOnMachine(1);
		getOnMachine(2);
		getOnMachine(3);
		getOnMachine(4);
		getOnMachine(5);
		driverFacade.click("#headerButton input:nth-child(4)");
	}

	@Test
	@DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
	public void machineCreateAndSearch() {
		driverFacade.get("http://localhost:" + port);
		driverFacade.write("#loginEmail", "bence@gmail.com");
		driverFacade.write("#password", "password");
		driverFacade.click("#login");
		driverFacade.text("#email", "bence@gmail.com");
		driverFacade.click("#uploadMoney");
		int actMoney = Integer.parseInt(driverFacade.visible("#spendingMoney").getText()) + 2000;
		driverFacade.write("#money", "2000");
		driverFacade.click("#upload");
		driverFacade.click("#closeUpload");
		driverFacade.hidden(".modal-backdrop");
		driverFacade.text("#spendingMoney", Integer.toString(actMoney));
		createAmusementParks("Jeni parkja", 49000, 5000, 100);
		driverFacade.click("#amusementParkShowSearchButton");
		searchAmusementPark("Jeni parkja", 40000, 50000, 4000, 5000, 100, 100);
		driverFacade.numberOfRowsInTable("#tableBody", 1);
		driverFacade.click("#tableBody td");
		driverFacade.click("#enterPark");
		createMachine("Retro körhinta", 100, 250, 10, 12, 10, MachineType.CAROUSEL);
		createMachine("Mágikus dodgem", 150, 250, 10, 12, 10, MachineType.DODGEM);
		createMachine("Gokart", 150, 250, 10, 12, 10, MachineType.GOKART);
		createMachine("Titanic", 150, 250, 10, 12, 10, MachineType.SHIP);
		createMachine("Hullámvasút", 150, 250, 10, 12, 10, MachineType.ROLLER_COASTER);
		createMachine("Dodgem", 120, 1500, 25, 10, 10, MachineType.DODGEM);
		createMachine("Magic ship", 100, 1100, 20, 16, 14, MachineType.SHIP);
		createMachine("Liluom", 90, 1200, 20, 15, 15, MachineType.CAROUSEL);
		createMachine("Dangerous", 110, 1300, 18, 16, 24, MachineType.ROLLER_COASTER);
		createMachine("FlashKart", 150, 1400, 13, 14, 25, MachineType.GOKART);
		createMachine("Hyperion", 120, 1500, 12, 12, 20, MachineType.ROLLER_COASTER);
		createMachine("Carousel", 30, 900, 8, 6, 10, MachineType.CAROUSEL);
		createMachine("Europeum", 170, 1600, 10, 14, 19, MachineType.GOKART);
		createMachine("Spongebob", 140, 1700, 11, 8, 15, MachineType.SHIP);
		driverFacade.click("#headerButton input:nth-child(3)");
		searchMachine("a", 30, 100, 250, 1000, 5, 10, 6, 14, 6, 10, "");
		driverFacade.numberOfRowsInTable("#tableBody", 2);
		clearSearchMachine();
		searchMachine("a", 50, 100, 250, 1000, 5, 10, 6, 14, 6, 10, "CAROUSEL");
		driverFacade.numberOfRowsInTable("#tableBody", 1);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 2000, 5, 30, 0, 21, 5, 30, "");
		driverFacade.text("#numberOfPage", "1/2");
		clearSearchMachine();
		searchMachine("", 150, 200, 50, 2000, 5, 30, 0, 21, 5, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 6);
		clearSearchMachine();
		searchMachine("", 20, 100, 50, 2000, 5, 30, 0, 21, 5, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 4);
		clearSearchMachine();
		searchMachine("", 20, 200, 1000, 2000, 5, 30, 0, 21, 5, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 8);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 500, 5, 30, 0, 21, 5, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 5);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 2000, 25, 30, 0, 21, 5, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 1);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 2000, 5, 12, 0, 21, 5, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 9);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 2000, 5, 30, 16, 21, 5, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 2);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 2000, 5, 30, 0, 10, 5, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 3);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 2000, 5, 30, 0, 21, 25, 30, "");
		driverFacade.numberOfRowsInTable("#tableBody", 1);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 2000, 5, 30, 0, 21, 5, 15, "");
		driverFacade.numberOfRowsInTable("#tableBody", 10);
		clearSearchMachine();
		searchMachine("", 20, 200, 50, 2000, 5, 30, 0, 21, 5, 30, "SHIP");
		driverFacade.numberOfRowsInTable("#tableBody", 3);
		createMachine("Dodgem", 120, 1500, 25, 10, 10, MachineType.DODGEM);
		createMachine("Magic ship", 100, 1100, 20, 16, 14, MachineType.SHIP);
		createMachine("Liluom", 90, 1200, 20, 15, 15, MachineType.CAROUSEL);
		createMachine("Dangerous", 110, 1300, 18, 16, 24, MachineType.ROLLER_COASTER);
		createMachine("FlashKart", 150, 1400, 13, 14, 25, MachineType.GOKART);
		createMachine("Hyperion", 120, 1500, 12, 12, 20, MachineType.ROLLER_COASTER);
		createMachine("Carousel", 30, 900, 8, 6, 10, MachineType.CAROUSEL);
		createMachine("Europeum", 170, 1600, 10, 14, 19, MachineType.GOKART);
		createMachine("Spongebob", 140, 1700, 11, 8, 15, MachineType.SHIP);
		driverFacade.text("#numberOfPage", "1/3");
		driverFacade.click("#last");
		driverFacade.text("#numberOfPage", "3/3");
		driverFacade.numberOfRowsInTable("#tableBody", 3);
		driverFacade.click("#left");
		driverFacade.text("#numberOfPage", "2/3");
		driverFacade.click("#right");
		driverFacade.text("#numberOfPage", "3/3");
		driverFacade.click("#first");
		driverFacade.text("#numberOfPage", "1/3");
		driverFacade.click("#headerButton input:nth-child(4)");

	}

	private void getOnMachine(int numberOfRow) {
		driverFacade.click("#tableBody tr:nth-child(" + numberOfRow + ") input");
		driverFacade.click("#machineModalExit");
		driverFacade.hidden(".modal-backdrop");

	}

	private void guestBookWrite(String textOfRegistry) {
		driverFacade.click("#guestBookButton");
		driverFacade.write("#guestBookText", textOfRegistry);
		driverFacade.click("#guestBookSave");
		driverFacade.click("#guestBookModal button");
		driverFacade.hidden(".modal-backdrop");
	}

	private void guestBookSearch(String timestampMin, String timestampMax, String visitor, String text) {
		driverFacade.write("#guestBookSearchTimestampMin", timestampMin);
		driverFacade.write("#guestBookSearchTimestampMax", timestampMax);
		driverFacade.write("#guestBookSearchVisitorEmail", visitor);
		driverFacade.write("#guestBookSearchText", text);
		driverFacade.click("#guestBookSearchButton");
		clearGuestBookSearch();
	}

	private void clearGuestBookSearch() {
		driverFacade.deleteText("#guestBookSearchTimestampMin");
		driverFacade.deleteText("#guestBookSearchTimestampMax");
		driverFacade.deleteText("#guestBookSearchVisitorEmail");
		driverFacade.deleteText("#guestBookSearchText");

	}

	private void createAmusementParks(String parkName, int capital, int totalArea, int entanceFee) {
		driverFacade.click("#headerButton input:nth-child(2)");
		driverFacade.write("#createAmusementParkName", "" + parkName);
		driverFacade.write("#createAmusementParkCapital", "" + capital);
		driverFacade.write("#createAmusementParkTotalArea", "" + totalArea);
		driverFacade.write("#createAmusementParkEntranceFee", "" + entanceFee);
		driverFacade.click("#createAmusementParkButton");
		driverFacade.hidden(".modal-backdrop");
	}

	private void searchAmusementPark(String name, int capitalMin, int capitalMax, int totalAreaMin, int totalAreaMax,
			int entranceFeeMin, int entranceFeeMax) {
		clearSearchAmusementPark();
		driverFacade.write("#searchName", name);
		driverFacade.write("#searchCapitalMin", Integer.toString(capitalMin));
		driverFacade.write("#searchCapitalMax", Integer.toString(capitalMax));
		driverFacade.write("#searchTotalAreaMin", Integer.toString(totalAreaMin));
		driverFacade.write("#searchTotalAreaMax", Integer.toString(totalAreaMax));
		driverFacade.write("#searchEntranceFeeMin", Integer.toString(entranceFeeMin));
		driverFacade.write("#searchEntranceFeeMax", Integer.toString(entranceFeeMax));
		driverFacade.click("#amusementParkSearchButton");
	}

	private void clearSearchAmusementPark() {
		driverFacade.deleteText("#searchName");
		driverFacade.deleteText("#searchCapitalMin");
		driverFacade.deleteText("#searchCapitalMax");
		driverFacade.deleteText("#searchTotalAreaMin");
		driverFacade.deleteText("#searchTotalAreaMax");
		driverFacade.deleteText("#searchEntranceFeeMin");
		driverFacade.deleteText("#searchEntranceFeeMax");

	}

	public void createMachine(String name, int size, int price, int numberOfSeats, int minimumRequiredAge,
			int ticketPrice, MachineType type) {
		driverFacade.click("#headerButton input:nth-child(2)");
		driverFacade.write("#machineCreateFantasyName", name);
		driverFacade.write("#machineCreateSize", Integer.toString(size));
		driverFacade.write("#machineCreatePrice", Integer.toString(price));
		driverFacade.write("#machineCreateNumberOfSeats", Integer.toString(numberOfSeats));
		driverFacade.write("#machineCreateMinimumRequiredAge", Integer.toString(minimumRequiredAge));
		driverFacade.write("#machineCreateTicketPrice", Integer.toString(ticketPrice));
		driverFacade.select("#machineCreateType", type.toString());
		driverFacade.click("#machineCreateButton");
		driverFacade.hidden(".modal-backdrop");

	}

	public void searchMachine(String name, int sizeMin, int sizeMax, int priceMin, int priceMax, int numberOfSeatsMin,
			int numberOfSeatsMax, int minimumRequiredAgeMin, int minimumRequiredAgeMax, int ticketPriceMin,
			int ticketPriceMax, String type) {
		driverFacade.write("#machineSearchFantasyName", name);
		driverFacade.write("#machineSearchSizeMin", Integer.toString(sizeMin));
		driverFacade.write("#machineSearchSizeMax", Integer.toString(sizeMax));
		driverFacade.write("#machineSearchPriceMin", Integer.toString(priceMin));
		driverFacade.write("#machineSearchPriceMax", Integer.toString(priceMax));
		driverFacade.write("#machineSearchNumberOfSeatsMin", Integer.toString(numberOfSeatsMin));
		driverFacade.write("#machineSearchNumberOfSeatsMax", Integer.toString(numberOfSeatsMax));
		driverFacade.write("#machineSearchMinimumRequiredAgeMin", Integer.toString(minimumRequiredAgeMin));
		driverFacade.write("#machineSearchMinimumRequiredAgeMax", Integer.toString(minimumRequiredAgeMax));
		driverFacade.write("#machineSearchTicketPriceMin", Integer.toString(ticketPriceMin));
		driverFacade.write("#machineSearchTicketPriceMax", Integer.toString(ticketPriceMax));
		driverFacade.select("#machineSearchType", type);
		driverFacade.click("#machineSearchButton");
	}

	private void clearSearchMachine() {
		driverFacade.deleteText("#machineSearchFantasyName");
		driverFacade.deleteText("#machineSearchSizeMin");
		driverFacade.deleteText("#machineSearchSizeMax");
		driverFacade.deleteText("#machineSearchPriceMin");
		driverFacade.deleteText("#machineSearchPriceMax");
		driverFacade.deleteText("#machineSearchNumberOfSeatsMin");
		driverFacade.deleteText("#machineSearchNumberOfSeatsMax");
		driverFacade.deleteText("#machineSearchMinimumRequiredAgeMin");
		driverFacade.deleteText("#machineSearchMinimumRequiredAgeMax");
		driverFacade.deleteText("#machineSearchTicketPriceMin");
		driverFacade.deleteText("#machineSearchTicketPriceMax");
		driverFacade.select("#machineSearchType", "");
	}

}
