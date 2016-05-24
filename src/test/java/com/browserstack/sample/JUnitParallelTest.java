package com.browserstack.sample;

import com.browserstack.sample.runner.Parallelized;
import io.undertow.Undertow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;


@RunWith(Parallelized.class)
public class JUnitParallelTest {
    private static AtomicInteger NEXT_PORT = new AtomicInteger(8080);

    private String browserName;
    private String browserVersion;
    private String os;
    private String osVersion;

    private Undertow webServer;
    private WebDriver driver;
    private int port;

    public JUnitParallelTest(String browser, String browserVersion, String os, String osVersion) {
        this.browserName = browser;
        this.browserVersion = browserVersion;
        this.os = os;
        this.osVersion = osVersion;
    }

    @Before
    public void setUp() throws Exception {
        port = NEXT_PORT.incrementAndGet();

        System.out.println("Starting server on port: " + port);
        webServer = SampleServer.newServer("localhost", port);
        webServer.start();

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browser", browserName);
        capabilities.setCapability("browser_version", browserVersion);
        capabilities.setCapability("os", os);
        capabilities.setCapability("os_version", osVersion);
        capabilities.setCapability("browserstack.local", System.getenv("BROWSERSTACK_LOCAL"));
        capabilities.setCapability("browserstack.localIdentifier", System.getenv("BROWSERSTACK_LOCAL_IDENTIFIER"));

        capabilities.setCapability("build", "Sample JUnit Tests");
        capabilities.setCapability("name", "Sample JUnit Parallel Tests");

        String username = System.getenv("BROWSERSTACK_USER");
        String accessKey = System.getenv("BROWSERSTACK_ACCESSKEY");

        driver = new RemoteWebDriver(
                new URL(String.format("https://%s:%s@hub.browserstack.com/wd/hub", username, accessKey)), capabilities);
    }

    @Test
    public void testSimple() throws Exception {
        driver.get("http://localhost:" + port);
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

    @Parameterized.Parameters
    public static LinkedList getEnvironments() throws Exception {
        LinkedList<String[]> env = new LinkedList<String[]>();
        env.add(new String[]{"chrome", "49", "Windows", "7"});
        env.add(new String[]{"firefox", "46", "Windows", "8"});
        env.add(new String[]{"ie", "10", "Windows", "7"});
        env.add(new String[]{"ie", "11", "Windows", "10"});
        return env;
    }
}
