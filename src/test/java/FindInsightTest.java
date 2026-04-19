import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;
import static org.junit.Assert.assertTrue;

public class FindInsightTest extends BaseTest {

    @Test
    public void testInsight_ListArticles() {
        System.out.println("[TS-06-01] Переход в раздел Insights...");
        driver.get("https://www.xing.com/insights");
        handleCookieBanner();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        List<WebElement> articles = driver.findElements(By.xpath(
                "//*[@id='content']//div[contains(@class, 'card') or contains(@class, 'teaser')] | " +
                        "//*[@id='content']//article"
        ));

        System.out.println("Найдено элементов: " + articles.size());
        assertTrue("Список статей пуст", articles.size() > 0);
        System.out.println("[TS-08-01] Успех.");
    }

    @Test
    public void testInsight_OpenArticle() {
        System.out.println("[TS-08-02] Открытие статьи...");
        driver.get("https://www.xing.com/insights");
        handleCookieBanner();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("content")));
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        boolean clicked = false;
        String urlBefore = driver.getCurrentUrl();

        try {
            String baseXPath = "/html/body/div[1]/div[2]/div/div/main/section/div/div/div/div[1]/section/div/div/div/div[1]";
            By linkInBlock = By.xpath(baseXPath + "//a[@href]");

            System.out.println("Ищу ссылку внутри указанного блока...");
            WebElement articleLink = wait.until(ExpectedConditions.elementToBeClickable(linkInBlock));

            js.executeScript("arguments[0].scrollIntoView(true);", articleLink);
            Thread.sleep(500);

            System.out.println("Клик по ссылке: " + articleLink.getAttribute("href"));
            js.executeScript("arguments[0].click();", articleLink);

            clicked = true;

        } catch (Exception e) {
            System.out.println("Не удалось кликнуть по точному XPath. Пробую запасной вариант...");
            try {
                WebElement fallback = driver.findElement(By.xpath("//*[@id='content']//a[contains(@href, '/magazine/') or contains(@href, '/stories/')]"));
                js.executeScript("arguments[0].click();", fallback);
                handleCookieBanner();
                clicked = true;
            } catch (Exception ex) {
                System.out.println("Запасной вариант тоже не сработал.");
            }
        }

        assertTrue("Не удалось совершить клик", clicked);

        System.out.println("Ожидание смены URL...");
        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlBefore)));
            System.out.println("URL изменился на: " + driver.getCurrentUrl());
        } catch (Exception e) {
            System.out.println("URL не изменился за время ожидания.");
            assertTrue("Переход не произошел", false);
        }

        boolean hasTitle = isElementPresent(By.xpath("//h1"), 10);
        assertTrue("Статья не открылась (нет заголовка H1)", hasTitle);

        System.out.println("[TS-08-02] Успех: Статья открыта.");

        try { Thread.sleep(10000); } catch (InterruptedException e) {}
    }
}