package org.example;

import junit.framework.Assert;
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

        driver.get("https://automationexercise.com/login");

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


//
//            @ParameterizedTest
//    @CsvSource({
//            "'', analia@gmail.com, name", //falla el nombre
//            "'', '', ambos", // fallan ambos
//            "Emiliano, '', email", // falla el email
//            "Emiliano, emiliano.com, email", // falla el email
//            // "Analia Bauer , bauerany@gmail.com, emailExist", //caso de usuario ya registrado
//         "Analia Bauer, analiabauer.testing92@gmail.com, none" // caso valido
//
//    })
//    void testValidacionCamposRegistro(String name, String email, String casoEsperado) {
//        WebElement loginLink = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div/div/div[2]/div/ul/li[4]/a"));
//        loginLink.click();
//
//        WebElement inputName = driver.findElement(By.name("name"));
//        WebElement inputEmail = driver.findElement(By.cssSelector("input[data-qa='signup-email']"));
//        WebElement signUpButton = driver.findElement(By.cssSelector("button[data-qa='signup-button']"));
//
//        inputName.clear();
//        inputEmail.clear();
//
//        inputName.sendKeys(name != null ? name : "");
//        inputEmail.sendKeys(email != null ? email : "");
//        signUpButton.click();
//
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        boolean nameValido = (Boolean) js.executeScript("return arguments[0].checkValidity();", inputName);
//        boolean emailValido = (Boolean) js.executeScript("return arguments[0].checkValidity();", inputEmail);
//
//        switch (casoEsperado.toLowerCase()) {
//            case "name":
//                Assertions.assertFalse(nameValido, "El campo 'name' debería ser inválido");
//                Assertions.assertTrue(emailValido, "El campo 'email' debería ser válido");
//                break;
//            case "email":
//                Assertions.assertTrue(nameValido, "El campo 'name' debería ser válido");
//                Assertions.assertFalse(emailValido, "El campo 'email' debería ser inválido");
//                break;
//            case "ambos":
//                Assertions.assertFalse(nameValido, "El campo 'name' debería ser inválido");
//                Assertions.assertFalse(emailValido, "El campo 'email' debería ser inválido");
//                break;
//            case "emailexist":
//                String mensajeError = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                        By.xpath("//p[contains(text(),'Email Address already exist')]"))).getText();
//
//                Assertions.assertEquals("Email Address already exist!", mensajeError);
//                break;
//            case "none":
//                Assertions.assertTrue(nameValido, "El campo 'name' debería ser válido");
//                Assertions.assertTrue(emailValido, "El campo 'email' debería ser válido");
//
//                new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlToBe("https://automationexercise.com/signup"));
//                Assertions.assertEquals("https://automationexercise.com/signup", driver.getCurrentUrl());
//                break;
//
//            default:
//                Assertions.fail("Caso de prueba desconocido: " + casoEsperado);
//        }
//    }


    // "Analia Bauer , bauerany@gmail.com, emailExist", //caso de usuario ya registrado

    @Test
    void registrationWithExistingUser() {

        completeForm("Analia Bauer", "bauerany@gmail.com");

        String messageAlreadyExist = wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.xpath("//*[@id=\"form\"]/div/div/div[3]/div/form/p"))).getText();
        Assert.assertEquals("Email Address already exist!", messageAlreadyExist);

    }

    @Test
    void registrationWithValidUser(){

        completeForm("Analia Bauer", "analiabauer" + System.currentTimeMillis() + "@gmail.com");

        wait.until(ExpectedConditions.urlToBe("https://automationexercise.com/signup"));
        Assertions.assertEquals("https://automationexercise.com/signup", driver.getCurrentUrl());
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

