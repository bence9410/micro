package hu.beni.amusementpark.test.ui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import hu.beni.amusementpark.helper.DriverFacade;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SeleniumTests {

	static {
		System.setProperty("webdriver.gecko.driver", "/opt/geckodriver");
	}

	@LocalServerPort
	private int port;

	@Test
	public void test() throws InterruptedException {

		DriverFacade driverFacade = new DriverFacade(new FirefoxDriver());
		driverFacade.get("http://localhost:" + port);
		driverFacade.write("#loginEmail", "hello Jeni!");

		Thread.sleep(10000);

		driverFacade.quit();

	}
}
