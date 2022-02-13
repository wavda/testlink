package operations;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import testlink.api.java.client.TestLinkAPIResults;

import java.util.Optional;

public class CustomTestWatcher implements TestWatcher {
    @Override
    public void testFailed(ExtensionContext extensionContext, Throwable throwable) {
        System.out.println("Fail!");
        BaseSetup.testResult = TestLinkAPIResults.TEST_FAILED;
        BaseSetup.testNote =
                "Platform: "+ getPlatform() +
                "\nCurrent url:" + BaseSetup.currentUrl +
                "\n=========================\nExecution log: \n" + throwable.toString() +
                "\n=========================\nBrowser log: \n " + BaseSetup.browserLogs;

        try {
            ReportResult.reportResult();
        } catch (Exception exception) {
            exception.printStackTrace();}
    }

    @Override
    public void testSuccessful(ExtensionContext extensionContext) {
        System.out.println("Pass!");
        BaseSetup.testResult = TestLinkAPIResults.TEST_PASSED;
        BaseSetup.testNote =
                "Platform: "+ getPlatform() +
                "\nCurrent url:" + BaseSetup.currentUrl +
                "\n=========================\n Browser log: \n " + BaseSetup.browserLogs;
        try {
            ReportResult.reportResult();
        } catch (Exception e) {
            e.printStackTrace(); }
    }

    private String getPlatform(){
        WebDriver driver = BaseSetup.driver;
        Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
        String browserName = caps.getBrowserName();
        String browserVersion = caps.getBrowserVersion();

        return browserName + " " + browserVersion;
    }
}