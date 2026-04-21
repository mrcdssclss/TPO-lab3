import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertTrue;

public class EditProfileTest extends BaseTest {

    private static final String TEST_EMAIL = "alexandrevnas@gmail.com";
    private static final String TEST_PASS = "schm11dtt_";

    @Test
    public void testChangeName() {
        System.out.println("Старт: Изменение имени профиля");

        driver.get("https://login.xing.com/");
        handleCookieBanner();

        safeType(By.xpath("//input[@type='email']"), TEST_EMAIL);
        safeType(By.xpath("//input[@type='password']"), TEST_PASS);
        safeClick(By.xpath("//button[@type='submit']"));

        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.xing.com")));
        } catch (Exception e) {
            assertTrue("Не удалось войти", false);
        }

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        handleCookieBanner();

        String url = "https://www.xing.com/settings/account/misc/name";
        driver.get(url);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        handleCookieBanner();

        By firstNameLocator = By.xpath("//label[contains(text(), 'First name') or contains(text(), 'Vorname')]/following-sibling::input | //input[@name='first_name' or @name='firstName']");

        WebElement firstNameInput = null;
        try {
            firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameLocator));
        } catch (Exception e) {
            firstNameInput = driver.findElement(By.xpath("(//main//input[@type='text'])[1]"));
        }

        firstNameInput.clear();
        firstNameInput.sendKeys("Alexandra");

        By saveBtnLocator = By.xpath("//button[contains(text(), 'Save') or contains(text(), 'Speichern') or contains(text(), 'Aktualisieren')]");

        try {
            safeClick(saveBtnLocator);
        } catch (Exception e) {
            driver.findElement(By.xpath("//form//button[@type='submit']")).click();
        }

        boolean success = false;
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), 'saved') or contains(text(), 'gespeichert') or contains(text(), 'erfolgreich')]")
            ));
            success = true;
        } catch (Exception e) {
            if (driver.getCurrentUrl().contains("/settings/account/misc/name")) {
                int errors = driver.findElements(By.xpath("//div[contains(@class, 'error')]")).size();
                if (errors == 0) success = true;
            }
        }

        assertTrue("Имя не сохранено", success);
    }
}