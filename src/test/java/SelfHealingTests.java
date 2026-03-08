
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import PageObjects.AddOwner;


public class SelfHealingTests {

    private static String siteURL = "http://localhost:8080";
    static WebDriver driver;
    static WebDriverWait wait;

    @BeforeAll
    public static void setUp() {
        // Setting up chrome for the tests.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterAll
    public static void tearDown() {
        //driver.close();
    }

    @Test
    void findOwner() {

        AddOwner ownerFind = new AddOwner(driver);

        ownerFind.loadHomePage(siteURL);
        ownerFind.navigateToFindOwnersPage();
        ownerFind.findOwnerOnPage("Kumar");

        ownerFind.navigateToFindOwnersPage();
        ownerFind.findOwnerOnPage("Sinha");

    }
}
