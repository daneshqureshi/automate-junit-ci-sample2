package com.browserstack.sample;

import io.undertow.Undertow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class JUnitTest {
    private static final int PORT = SampleServer.PORT;

    private Undertow webServer;
    private WebDriver driver;

    @Before
    public void setUp() throws Exception {
        System.out.println("Starting server on port: " + PORT);
        webServer = SampleServer.newServer("localhost", PORT);
        webServer.start();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browser", "firefox");
        capabilities.setCapability("os", "Windows");
        capabilities.setCapability("os_version", "7");
        capabilities.setCapability("browserstack.local", System.getenv("BROWSERSTACK_LOCAL"));
        capabilities.setCapability("browserstack.localIdentifier", System.getenv("BROWSERSTACK_LOCAL_IDENTIFIER"));

        capabilities.setCapability("build", "Sample JUnit Tests");
        capabilities.setCapability("name", "Sample JUnit Local Tests");

        String username = System.getenv("BROWSERSTACK_USER");
        String accessKey = System.getenv("BROWSERSTACK_ACCESSKEY");

        driver = new RemoteWebDriver(
                new URL(String.format("https://%s:%s@hub-cloud.browserstack.com/wd/hub", username, accessKey)), capabilities);
    }

    @Test
    public void testSimple() throws Exception {
        driver.get("http://localhost:" + PORT);
        String title = driver.getTitle();
        System.out.println("Page title is: " + title);
        assertEquals("BrowserStack", title);

        WebElement element = driver.findElement(By.tagName("h1"));
        assertEquals("Test Page", element.getText());
    }

    @After
    public void tearDown() throws Exception {
        if (driver != null) {
            driver.quit();
        }

        if (webServer != null) {
            webServer.stop();
        }
    }
}
