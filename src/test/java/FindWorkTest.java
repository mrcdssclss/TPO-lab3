import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.Assert.assertTrue;

public class FindWorkTest extends BaseTest {

    private static final String SEARCH_URL_WITH_QUERY = "https://www.xing.com/jobs/search/ki?nwt_nav=java&id=611a3ea544a872d89ad248b6cf7b4d9d&keywords=java";
    private static final String SEARCH_URL_EMPTY = "https://www.xing.com/jobs/search";

    @Test
    public void testSearchJobs_WithKeyword() {
        System.out.println("[TS-03-01] Поиск вакансий по запросу 'KI'...");

        driver.get(SEARCH_URL_WITH_QUERY);
        handleCookieBanner();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));

        boolean hasJobs = isElementPresent(
                By.xpath("//article | //div[contains(@class, 'job-card')] | //li[contains(@class, 'job')]"),
                5
        );

        assertTrue("Список вакансий по запросу не отображается", hasJobs);
        System.out.println("Успех: Вакансии по запросу найдены.");
    }

    @Test
    public void testSearchJobs_EmptyQuery() {
        System.out.println("[TS-03-02] Открытие общего списка вакансий (без запроса)...");

        driver.get(SEARCH_URL_EMPTY);
        handleCookieBanner();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));

        boolean pageLoaded = driver.findElement(By.tagName("main")).isDisplayed();

        boolean hasContent = isElementPresent(
                By.xpath("//article | //div[contains(@class, 'job')] | //h1"),
                5
        );

        assertTrue("Общий список вакансий не загрузился", pageLoaded && hasContent);
        System.out.println("Успех: Общий список открыт.");
    }
}