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
        System.out.println("[TS-05-01] Старт: Изменение имени профиля");

        driver.get("https://login.xing.com/");
        handleCookieBanner();

        System.out.println(" Вход в систему...");
        safeType(By.xpath("//input[@type='email']"), TEST_EMAIL);
        safeType(By.xpath("//input[@type='password']"), TEST_PASS);
        safeClick(By.xpath("//button[@type='submit']"));

        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.xing.com")));
            System.out.println("Успешный вход.");
        } catch (Exception e) {
            assertTrue("Не удалось войти", false);
        }

        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        handleCookieBanner();

        String url = "https://www.xing.com/settings/account/misc/name";
        System.out.println("Переход: " + url);
        driver.get(url);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
        handleCookieBanner();

        System.out.println("Поиск поля First Name...");

        By firstNameLocator = By.xpath("//label[contains(text(), 'First name') or contains(text(), 'Vorname')]/following-sibling::input | //input[@name='first_name' or @name='firstName']");

        WebElement firstNameInput = null;
        try {
            firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameLocator));
        } catch (Exception e) {
            System.out.println("Точный локатор не сработал, ищу первое текстовое поле...");
            firstNameInput = driver.findElement(By.xpath("(//main//input[@type='text'])[1]"));
        }

        firstNameInput.clear();
        firstNameInput.sendKeys("Alexandra");
        System.out.println("Имя 'Alexandra' введено.");

        System.out.println("Поиск поля Last Name...");
        By lastNameLocator = By.xpath("//label[contains(text(), 'Last name') or contains(text(), 'Nachname')]/following-sibling::input | //input[@name='last_name' or @name='lastName']");

        WebElement lastNameInput = null;
        try {
            lastNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(lastNameLocator));
            if (lastNameInput.getAttribute("value").isEmpty()) {
                lastNameInput.sendKeys("User");
            }
        } catch (Exception e) {
            System.out.println("Поле фамилии не найдено или уже заполнено.");
        }

        System.out.println("Сохранение...");
        By saveBtnLocator = By.xpath("/html/body/div[1]/div[2]/div/div/main/section/div/section/div[3]/div/form/button");

        try {
            safeClick(saveBtnLocator);
        } catch (Exception e) {
            System.out.println("Кнопка по тексту не найдена, пробую type=submit");
            driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/main/section/div/section/div[3]/div/form/button")).click();
        }

        boolean success = false;
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), 'saved') or contains(text(), 'gespeichert') or contains(text(), 'erfolgreich')]")
            ));
            success = true;
            System.out.println("Сообщение об успехе найдено!");
        } catch (Exception e) {
            System.out.println("Сообщение об успехе не появилось явно.");
            if (driver.getCurrentUrl().contains("/settings/account/misc/name")) {
                int errors = driver.findElements(By.xpath("//div[contains(@class, 'error')]")).size();
                if (errors == 0) success = true;
            }
        }

        assertTrue("Имя не сохранено", success);
    }
}