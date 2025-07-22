package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginTest {
    static WebDriver driver;
    static WebDriverWait wait;
    static Actions actions;
    static WebElement inputEmail;
    static WebElement inputPass;
    static WebElement loginButton;

    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);

    }

    @BeforeEach
    public void goToLoginPageAndFinElements(){
        driver.get("https://www.automationexercise.com/login");

        inputEmail = driver.findElement(By.cssSelector("input[type = 'email'][data-qa = 'login-email']"));
        inputPass = driver.findElement(By.cssSelector("input[type = 'password'][data-qa = 'login-password']"));
        loginButton = driver.findElement(By.cssSelector("button[type = 'submit'][data-qa = 'login-button']"));

    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }

    }

    @ParameterizedTest
    @CsvSource({
            //email - pass - validacionesEmail, validacionesPass
            "'', '123456', false, true",
            "bauerany@gmail.com, '', true, false", // falla el pass
            "'', '', false, false" // fallan ambos

//            "Emiliano, emiliano.com, true, false", // falla el email
    })
    void loginWiThEmptyFields(String name, String email, boolean validationEmail, boolean validationPass) {

        inputEmail.clear();
        inputPass.clear();

        inputEmail.sendKeys(name != null ? name : "");
        inputPass.sendKeys(email != null ? email : "");
        loginButton.click();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean validEmail = (Boolean) js.executeScript("return arguments[0].checkValidity();", inputEmail);
        boolean validPass = (Boolean) js.executeScript("return arguments[0].checkValidity();", inputPass);

        Assertions.assertEquals(validEmail, validationEmail);
        Assertions.assertEquals(validPass, validationPass);

    }

    @Test
    void successfulLogin(){

        inputEmail.sendKeys("bauerany@gmail.com");
        inputPass.sendKeys("123456");
        loginButton.click();

        wait.until(ExpectedConditions.urlToBe("https://www.automationexercise.com/"));
        Assertions.assertEquals("https://www.automationexercise.com/", driver.getCurrentUrl());

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[href='/logout']")));

    }
}
