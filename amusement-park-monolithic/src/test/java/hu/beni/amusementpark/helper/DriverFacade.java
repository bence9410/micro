package hu.beni.amusementpark.helper;

import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import java.io.File;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DriverFacade {

	private final WebDriver webDriver;
	private final WebDriverWait wait;

	public DriverFacade(WebDriver driver) {
		this.webDriver = driver;
		wait = new WebDriverWait(driver, 10);
	}

	public void get(String url) {
		webDriver.get(url);
	}

	public void quit() {
		webDriver.quit();
	}

	public WebDriver getWebDriver() {
		return webDriver;
	}

	public File takeScreenshot() {
		return TakesScreenshot.class.cast(webDriver).getScreenshotAs(OutputType.FILE);
	}

	public WebElement visible(String selector) {
		return wait.until(visibilityOfElementLocated(By.cssSelector(selector)));
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

	public void select(String selector, String value) {
		visible(selector);
		wait.until(driver -> {
			new Select(driver.findElement(By.cssSelector(selector))).selectByValue(value);
			return new Select(driver.findElement(By.cssSelector(selector))).getFirstSelectedOption()
					.getAttribute("value").equals(value);
		});
	}

	public void setAttribute(String id, String name, String value) {
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("document.getElementById('" + id + "').setAttribute('" + name + "', '" + value + "')");
	}

	public void text(String selector, String text) {
		wait.until(textToBe(By.cssSelector(selector), text));

	}

	public void deleteText(String selector) {
		visible(selector);
		wait.until(driver -> {
			driver.findElement(By.cssSelector(selector)).clear();
			return driver.findElement(By.cssSelector(selector)).getAttribute("value").equals("");
		});
	}

	public void hidden(String selector) {
		wait.until(invisibilityOfElementLocated(By.cssSelector(selector)));
	}

	public void notPresent(String selector) {
		wait.until(driver -> {
			return driver.findElements(By.cssSelector(selector)).size() == 0;
		});
	}

	public void numberOfRowsInTable(String selector, int number) {
		wait.until(new TableRow(selector, number));
	}

	private static class TableRow implements Function<WebDriver, Boolean> {

		private final String selector;
		private final int number;
		private int actualNumber;

		public TableRow(String selector, int number) {
			this.selector = selector;
			this.number = number;
		}

		@Override
		public Boolean apply(WebDriver driver) {
			actualNumber = driver.findElement(By.cssSelector(selector)).findElements(By.cssSelector("tr")).size();
			return actualNumber == number;
		}

		@Override
		public String toString() {

			return "The actual number of rows: " + actualNumber + ", expected number of rows: " + number;
		}

	}

}
