package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import PageObjects.SelfHealingLogic;


public class AddOwner {
    private final WebDriver driver ;
    private final WebDriverWait wait;

    private final By findOwnersLink = By.xpath("//a[@title='find owners']");
    private final By findByLastName = By.xpath("//input[@name='lastName']");
    private final By findOwnerButton = By.xpath("//butttton[@type='submit']");

    public AddOwner(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void loadHomePage(String url) {
        driver.get(url);
    }
    public void navigateToFindOwnersPage(){
        wait.until(ExpectedConditions.elementToBeClickable(findOwnersLink)).click();
    }

    public void findOwnerOnPage(String lastName){
        wait.until(ExpectedConditions.visibilityOfElementLocated(findByLastName));
        wait.until(ExpectedConditions.elementToBeClickable(findByLastName));

        WebElement lastNameBox = SelfHealingLogic.findElementByHealing(driver,findByLastName);
        lastNameBox.click();
        lastNameBox.sendKeys(lastName);

        WebElement ownerFindButton = SelfHealingLogic.findElementByHealing(driver,findOwnerButton);
        ownerFindButton.click();
    }
}