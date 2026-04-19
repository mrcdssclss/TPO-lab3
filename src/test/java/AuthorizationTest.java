import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertTrue;

public class AuthorizationTest extends BaseTest {

    private static final String VALID_EMAIL = "alexandrevnas@gmail.com";
    private static final String VALID_PASS = "schm11dtt_";

    private static final String INVALID_EMAIL = "wrong_user@test.com";
    private static final String INVALID_PASS = "WrongPassword123";

    @Test
    public void testLogin_Success() {
        System.out.println("[TS-01-01] Начало теста: Успешная авторизация");

        driver.get("https://login.xing.com/");
        handleCookieBanner();

        System.out.println("Заполнение формы верными данными...");

        safeType(By.xpath("//input[@type='email' or @name='username']"), VALID_EMAIL);
        safeType(By.xpath("//input[@type='password' or @name='password']"), VALID_PASS);

        System.out.println("Отправка формы...");
        WebElement submitBtn = driver.findElement(By.xpath("//button[@type='submit'] | //button[contains(text(), 'Log in')]"));
        submitBtn.click();

        System.out.println("Ожидание перехода в личный кабинет...");

        boolean isLogged = false;
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//img[contains(@alt, 'Profile') or contains(@src, 'profile')] | //div[contains(@class, 'user-name')] | //a[contains(@href, '/profile/')]")
            ));

            if (!driver.getCurrentUrl().contains("login")) {
                isLogged = true;
            }
        } catch (Exception e) {
            System.out.println("Элементы профиля не найдены или таймаут.");
        }

        assertTrue("Авторизация не удалась: элементы профиля не появились", isLogged);
        System.out.println("УСПЕХ: Пользователь авторизован.");
    }

    @Test
    public void testLogin_InvalidPassword() {
        System.out.println("[TS-01-03] Начало теста: Неверный пароль");

        driver.get("https://login.xing.com/");
        handleCookieBanner();

        System.out.println("Заполнение формы неверными данными...");

        safeType(By.xpath("//input[@type='email' or @name='username']"), INVALID_EMAIL);
        safeType(By.xpath("//input[@type='password' or @name='password']"), INVALID_PASS);

        System.out.println("Отправка формы...");
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(Keys.ENTER);

        System.out.println("Ожидание сообщения об ошибке...");

        boolean errorFound = isElementPresent(
                By.xpath("//*[contains(text(), 'incorrect') or contains(text(), 'falsch') or contains(@class, 'error-message') or contains(@class, 'alert-error')]"),
                10
        );

        if (errorFound) {
            System.out.println("УСПЕХ: Ошибка авторизации корректно отображена.");
        } else {
            System.out.println("ПРОВАЛ: Ошибка не появилась.");
        }

        assertTrue("Ошибка входа не отображена при неверном пароле", errorFound);
    }
}