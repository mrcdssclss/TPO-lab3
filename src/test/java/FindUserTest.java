import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import static org.junit.Assert.assertTrue;

public class FindUserTest extends BaseTest {

    private static final String TEST_EMAIL = "alexandrevnas@gmail.com";
    private static final String TEST_PASS = "schm11dtt_";

    @Test
    public void testFindUser_ByKeyword() {
        System.out.println("[TS-04-01] Начало поиска пользователей...");

        driver.get("https://login.xing.com/");
        handleCookieBanner();
        safeType(By.xpath("//input[@type='email']"), TEST_EMAIL);
        safeType(By.xpath("//input[@type='password']"), TEST_PASS);
        safeClick(By.xpath("//button[@type='submit']"));

        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("login.xing.com")));
        } catch (Exception e) {
            assertTrue("Не удалось войти для выполнения поиска", false);
        }
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        handleCookieBanner();

        driver.get("https://www.xing.com/search/members?keywords=Software%20Engineer");
        handleCookieBanner();
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        boolean resultsLoaded = isElementPresent(
                By.xpath("//a[contains(@href, '/profile/')]"),
                15
        );

        assertTrue("Список пользователей не отображается", resultsLoaded);
        System.out.println("Успех: Список пользователей найден.");

        System.out.println("Пауза 10 секунд для просмотра списка пользователей...");
        try { Thread.sleep(10000); } catch (InterruptedException e) {}
    }

    @Test
    public void testFindUser_OpenProfile() {
        System.out.println("[TS-04-02] Открытие профиля пользователя...");

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

        driver.get("https://www.xing.com/search/members?keywords=Developer");
        handleCookieBanner();
        try { Thread.sleep(3000); } catch (InterruptedException e) {}

        boolean clicked = false;
        try {
            List<WebElement> links = driver.findElements(By.xpath("//a[contains(@href, '/profile/')]"));

            if (!links.isEmpty()) {
                WebElement link = links.get(0);
                js.executeScript("arguments[0].scrollIntoView(true);", link);
                Thread.sleep(500);
                js.executeScript("arguments[0].click();", link);
                handleCookieBanner();
                clicked = true;
                System.out.println("Клик по профилю выполнен.");
            } else {
                System.out.println("Ссылки на профили не найдены.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при клике: " + e.getMessage());
        }

        assertTrue("Не удалось совершить клик по пользователю", clicked);

        boolean profileOpened = isElementPresent(By.xpath("//h1"), 15);
        boolean validUrl = driver.getCurrentUrl().contains("/profile/");

        assertTrue("Страница пользователя не открылась", profileOpened && validUrl);
        System.out.println("Успех: Профиль открыт.");

        System.out.println("Пауза 10 секунд для просмотра открытого профиля...");
        try { Thread.sleep(10000); } catch (InterruptedException e) {}
    }
}