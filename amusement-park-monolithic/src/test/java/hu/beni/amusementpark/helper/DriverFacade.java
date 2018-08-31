package hu.beni.amusementpark.helper;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverFacade {

	private final WebDriver webDriver;
	private final WebDriverWait wait;

	public DriverFacade(WebDriver driver) {
		this.webDriver = driver;
		wait = new WebDriverWait(driver, 5);
	}

	public void get(String url) {
		webDriver.get(url);
	}

	public void quit() {
		webDriver.quit();
	}

	public void visible(String selector) {
		wait.until(visibilityOfElementLocated(By.cssSelector(selector)));
	}

	public void write(String selector, String value) {
		visible(selector);
		wait.until(driver -> {
			driver.findElement(By.cssSelector(selector)).sendKeys(value);
			return driver.findElement(By.cssSelector(selector)).getAttribute("value").equals(value);
		});
	}

	public void click(String selector) {
		wait.until(elementToBeClickable(By.cssSelector(selector))).click();
	}

	public void text(String selector, String text) {
		wait.until(textToBe(By.cssSelector(selector), text));
	}

	public void hidden(String selector) {
		wait.until(invisibilityOfElementLocated(By.cssSelector(selector)));
	}

}
