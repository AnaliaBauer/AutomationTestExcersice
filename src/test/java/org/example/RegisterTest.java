package org.example;

import org.junit.jupiter.api.Assertions;
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
import java.util.Random;

public class RegisterTest {
    static WebDriver driver;
    static WebDriverWait wait;
    static Actions actions;
    static JavascriptExecutor js;
    static Random rand;
    static WebElement inputName;
    static WebElement inputEmail;
    static WebElement signUpButton;


    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);
        rand = new Random();
        js = (JavascriptExecutor) driver;

    }

    @BeforeEach
    public void goToLoginAndFinElements(){

        driver.get("https://www.automationexercise.com/login");

        inputName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("name")));
        inputEmail = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[data-qa='signup-email']")));
        signUpButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-qa='signup-button']")));

    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }

    }


    @ParameterizedTest
    @DisplayName("Registro con campos vacios")
    @CsvSource({
            //name - email - validacionesNAME, validacionesEMAIL
            "'', analia@gmail.com,false, true",
            "'', '', false, false", // fallan ambos
            "Emiliano, '', true, false", // falla el email
            "Emiliano, emiliano.com, true, false", // falla el email


    })
    void registerWiThEmptyFields(String name, String email, boolean validationName, boolean validationEmail) {

        inputName.clear();
        inputEmail.clear();

        inputName.sendKeys(name != null ? name : "");
        inputEmail.sendKeys(email != null ? email : "");
        signUpButton.click();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean nameValido = (Boolean) js.executeScript("return arguments[0].checkValidity();", inputName);
        boolean emailValido = (Boolean) js.executeScript("return arguments[0].checkValidity();", inputEmail);

        Assertions.assertEquals(nameValido, validationName);
        Assertions.assertEquals(emailValido, validationEmail);

    }


    @Test
    @DisplayName("Registro con usuario existente")
    void registrationWithExistingUser() {

        completeForm("Analia Bauer", "bauerany@gmail.com");

        String messageAlreadyExist = wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.xpath("//*[@id=\"form\"]/div/div/div[3]/div/form/p"))).getText();
        Assertions.assertEquals("Email Address already exist!", messageAlreadyExist);

    }

    @Test
    @DisplayName("Registro con usuario valido")
    void registrationWithValidUser(){

        completeForm("Analia Bauer", "analiabauer" + System.currentTimeMillis() + "@gmail.com");

        wait.until(ExpectedConditions.urlToBe("https://www.automationexercise.com/signup"));
        Assertions.assertEquals("https://www.automationexercise.com/signup", driver.getCurrentUrl());
    }


    //Metodo para completar formulario inicial
    public void completeForm(String name, String email){

        inputName.clear();
        inputEmail.clear();
        inputName.sendKeys(name);
        inputEmail.sendKeys(email);
        signUpButton.click();

    }

}

