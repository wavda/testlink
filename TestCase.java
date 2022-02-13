package operations;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.percy.selenium.Percy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

@ExtendWith(CustomTestWatcher.class)
public class BaseSetup {
	public static ObjectParser parser;
	public static WebDriver driver;
	public static WebDriverWait wait;
	public static JavascriptExecutor js;
	public static BaseSetup resource;
	public static Properties properties;
	public static Logger log;

	public static String testCaseId;
	public static String testResult;
	public static String testNote;

	public static String browserLogs;

	@BeforeEach
	public void before() throws Exception {
		driver = getDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(20));
		parser = new ObjectParser("src/test/resources/object.properties");
		js = (JavascriptExecutor) driver;
		log = Logger.getLogger("devpinoyLogger");

		resource = new BaseSetup();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		properties =  new Properties();
		properties.load(new FileInputStream("src/test/resources/config.properties"));

		driver.get(getUrl());

		wait.until(driver -> (js.executeScript("return document.readyState").equals("complete")));
		operations.RecordTest.startRecording(testCaseId); //works for non-headless browser execution
	}

	@AfterEach
	public void after() throws Exception {
		String classname = getClass().getSimpleName();
		TakeScreenshot.takeScreenshot(classname);
	 	operations.RecordTest.stopRecording(); //works only for non-headless browser execution

		getBrowserLogs();
		currentUrl = driver.getCurrentUrl();
		driver.quit();
	}
	
	@Test
	@DisplayName("Navigate to Google")
	@Tags(value = {@Tag("regression"), @Tag("smoke")})
	public void testOrder() {
		//Select Plan and Duration
		driver.get("https://www.google.com");
		Assertions.assertTrue(driver.getTitle().contains("Google"));
	}

	public static void getBrowserLogs() {
		LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
		for (LogEntry entry : logEntries) {
			browserLogs = new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage();
		}
	}

	public WebDriver getDriver() throws Exception {
		switch (System.getProperty("browser").toLowerCase()) {
			case "chrome":
				System.out.println("Chrome was chosen!");
				WebDriverManager.chromedriver().setup();
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--window-size=1366,667");
				options.addArguments("--headless");
				options.addArguments("--disable-gpu");
				return new ChromeDriver(options);
			case "firefox":
				System.out.println("Firefox was chosen!");
				WebDriverManager.firefoxdriver().setup();
				FirefoxOptions options1 = new FirefoxOptions();
				options1.setHeadless(true);
				return new FirefoxDriver(options1);
			case "node-chrome":
				DesiredCapabilities dc = new DesiredCapabilities();
				dc.setBrowserName(BrowserType.CHROME);
				return new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), dc);
			default:
				throw new RuntimeException("Unsupported browser! Will not start any browser!");
		}
	}
}
