import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    protected Map<String, Object> vars;

    protected String getBrowser() {
        return System.getProperty("browser", "chrome");
    }

    @Before
    public void setUp() {
        String browser = getBrowser();

        if ("chrome".equalsIgnoreCase(browser)) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-notifications", "--start-maximized");
            driver = new ChromeDriver(options);
        } else {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            options.addPreference("permissions.default.image", 2);
            driver = new FirefoxDriver(options);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        js = (JavascriptExecutor) driver;
        vars = new HashMap<>();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void handleCookieBanner() {
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        String script =
                "var selectors = [" +
                        "'#usercentrics-root', " +
                        "'#onetrust-banner-sdk', " +
                        "'#cookie-banner', " +
                        "'.consent-layer', " +
                        "'.cookie-banner', " +
                        "'div[class*=\"consent\"]', " +
                        "'div[class*=\"cookie\"]'" +
                        "];" +
                        "selectors.forEach(function(sel) {" +
                        "   var elements = document.querySelectorAll(sel);" +
                        "   elements.forEach(function(el) {" +
                        "       el.style.display = 'none';" +
                        "       el.remove();" +
                        "   });" +
                        "});" +
                        "var btns = document.querySelectorAll('button');" +
                        "for(var i=0; i<btns.length; i++) {" +
                        "   var txt = btns[i].innerText.toLowerCase();" +
                        "   if(txt.includes('accept') || txt.includes('akzeptieren') || txt.includes('zustimmen')) {" +
                        "       btns[i].click();" +
                        "   }" +
                        "}";

        try {
            js.executeScript(script);
        } catch (Exception e) {
        }

        try { Thread.sleep(1000); } catch (InterruptedException e) {}
    }

    protected void safeClick(By locator) {
        try {
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            js.executeScript("arguments[0].scrollIntoView(true);", el);
            el.click();
        } catch (ElementClickInterceptedException e) {
            jsClick(locator);
        }
    }

    protected void jsClick(By locator) {
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        js.executeScript("arguments[0].click();", el);
    }

    protected void safeType(By locator, String text) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(text);
    }

    protected boolean isElementPresent(By locator, int timeoutSec) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}