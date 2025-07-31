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
import java.util.List;
import java.util.Random;


public class ProductsTest {

    static WebDriver driver;
    static WebDriverWait wait;
    static String currentURL;
    static Actions actions;
    static JavascriptExecutor js;
    static Random rand;


    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);
        rand = new Random();
        js = (JavascriptExecutor) driver;

        driver.get("https://automationexercise.com/");

        login();

    }

    @AfterAll
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }

    }

    @Test
    @DisplayName("Agregar producto al carrito")
    void addToCart() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Ir a la sección de productos
        WebElement productsLink = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div/div/div[2]/div/ul/li[2]/a"));
        productsLink.click();

        currentURL = driver.getCurrentUrl();
        Assertions.assertEquals("https://automationexercise.com/products", currentURL);

        // Hover sobre la tarjeta del primer producto
        List<WebElement> tarjetas = driver.findElements(By.cssSelector(".product-image-wrapper"));
        WebElement firstProduct = tarjetas.get(0);

        //Hacer hover sobre la primera tarjeta
        actions.moveToElement(firstProduct).perform();

        // Esperar a que el overlay se vuelva visible
        WebElement overlay = firstProduct.findElement(By.cssSelector(".product-overlay"));
        wait.until(ExpectedConditions.visibilityOf(overlay));

        //Eliminar anuncios que puedan interceptar el click
        js.executeScript("document.querySelectorAll('iframe[id^=\"aswift_\"]').forEach(el => el.remove());");

        // Ahora buscar el botón add to cart y hacer click
        WebElement botonAddToCart = firstProduct.findElement(By.cssSelector(".add-to-cart"));
        js.executeScript("arguments[0].click();", botonAddToCart);

        // Confirmación de modal
        WebElement confirmationModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("modal-content")));
        Assertions.assertNotNull(confirmationModal);

        WebElement viewCart = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//u[text()='View Cart']")));
        viewCart.click();

        currentURL = driver.getCurrentUrl();
        Assertions.assertEquals("https://automationexercise.com/view_cart", currentURL);

        WebElement productInCart = driver.findElement(By.id("product-1"));
        Assertions.assertNotNull(productInCart);

    }

    @ParameterizedTest
    @DisplayName("Buscar productos")
    @CsvSource({
            "jeans, 3",
            "tshirt, 6",
            "dress, 9",
            "tops, 13",
            "socks, 0",
            "hodies, 0"
    })
    void searchproduct(String tag, int quantityExpected) {
        WebElement productsLink = driver.findElement(By.cssSelector("li>a[href='/products']"));
        productsLink.click();

        String productsPage = driver.getCurrentUrl();
        Assertions.assertEquals("https://automationexercise.com/products", productsPage);

        WebElement searchBox = driver.findElement(By.id("search_product"));
        WebElement searchButton = driver.findElement(By.id("submit_search"));

        searchBox.sendKeys(tag);
        searchButton.click();

        String searchTitle = driver.findElement(By.cssSelector("h2.title.text-center")).getText();
        Assertions.assertEquals("SEARCHED PRODUCTS", searchTitle);

        List<WebElement> productsFound = driver.findElements(By.className("product-image-wrapper"));
        Assertions.assertEquals(quantityExpected, productsFound.size());

    }

    @Test
    @DisplayName("Enviar formulario de contacto")
    void contactUsForm() {
        //Se busca el elemento link a la pagina Contact Us y se verifica estar en la pagina correcta
        WebElement contactUs = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div/div/div[2]/div/ul/li[9]/a"));
        contactUs.click();
        currentURL = driver.getCurrentUrl();
        Assertions.assertEquals("https://automationexercise.com/contact_us", currentURL);
        //Busqueda de elementos del formulario de contacto
        WebElement name = driver.findElement(By.name("name"));
        WebElement email = driver.findElement(By.name("email"));
        WebElement subject = driver.findElement(By.name("subject"));
        WebElement messaje = driver.findElement(By.name("message"));
        WebElement file = driver.findElement(By.name("upload_file"));
        WebElement submitMessage = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[data-qa='submit-button']")));
        //Envio de informacion a los input
        name.sendKeys("Analia Bauer");
        email.sendKeys("analiabauer.testing@gmail.com");
        subject.sendKeys("Consulta precios por mayor");
        messaje.sendKeys("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut.");
        file.sendKeys("C:\\Users\\Analia\\Documents\\Documentos de prueba\\PDF Pruebas.pdf");
        submitMessage.click();
        driver.switchTo().alert().accept();
        //Se valida resultado final
        String successMessage = driver.findElement(By.cssSelector("div.status.alert.alert-success")).getText();
        Assertions.assertEquals("Success! Your details have been submitted successfully.", successMessage, "El texto no coincide");

    }

    public static void login() {
        driver.get("https://automationexercise.com/");
        WebElement loginLink = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div/div/div[2]/div/ul/li[4]/a"));
        loginLink.click();
        WebElement inputEmail = driver.findElement(By.name("email"));
        WebElement inputPassword = driver.findElement(By.name("password"));
        inputEmail.sendKeys("analiabauer.testing1@gmail.com");
        inputPassword.sendKeys("Admin123");
        WebElement submitButton = driver.findElement(By.xpath("//*[@id=\"form\"]/div/div/div[1]/div/form/button"));
        submitButton.click();

        WebElement loggedInAs = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(), 'Logged in as')]")
        ));
        Assertions.assertTrue(loggedInAs.isDisplayed(), "Login no exitoso");
    }

    public void addProductToCart() {

        // Ir a la sección de productos
        WebElement productLink = driver.findElement(By.cssSelector("a[href='/products']"));
        productLink.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-image-wrapper")));

        //Eliminar anuncios que puedan interceptar el click
        js.executeScript("document.querySelectorAll('iframe[id^=\"aswift_\"]').forEach(el => el.remove());");

        //Obtener la lista de todos los productos en pantalla
        List<WebElement> products = driver.findElements(By.cssSelector(".product-image-wrapper"));

        // Agarrar un producto random
        int randomIndex = rand.nextInt(products.size());

        WebElement viewProduct = products.get(randomIndex);
        WebElement viewLink = viewProduct.findElement(By.tagName("a"));

        js.executeScript("arguments[0].scrollIntoView(true);", viewLink);
        viewLink.click();

        // Agregar al carrito
        try {
            WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn.btn-default.cart")));
            addToCartButton.click();
        } catch (Exception e) {

        }

        WebElement confirmationModal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("modal-content")));

        Assertions.assertNotNull(confirmationModal);

        WebElement continueShoppingButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.btn.btn-success.close-modal.btn-block")
        ));
        continueShoppingButton.click();

    }

        @Test
        @DisplayName("Eliminar un producto del carrito")
        void deleteProductToCart() {
            addProductToCart();
            WebElement cart = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div/div/div[2]/div/ul/li[3]/a"));
            cart.click();
            currentURL = driver.getCurrentUrl();
            Assertions.assertEquals("https://automationexercise.com/view_cart", currentURL);

            //Obtener la lista de botones Eliminar
            List<WebElement> deleteButtons = driver.findElements(By.cssSelector("a.cart_quantity_delete"));

            Assertions.assertFalse(deleteButtons.isEmpty(), "No hay productos en el carrito");

            //Guardar la cantidad en una variable
            int countButtons = deleteButtons.size();
            int randomIndex = rand.nextInt(countButtons);

            //Eliminar un producto random
            WebElement randomDeleteButton = deleteButtons.get(randomIndex);
            randomDeleteButton.click();

        }

        @Test
        @DisplayName("Pagar un producto")
        void checkout() {
            addProductToCart();
            WebElement cart = driver.findElement(By.xpath("//*[@id=\"header\"]/div/div/div/div[2]/div/ul/li[3]/a"));
            cart.click();
            WebElement checkoutButton = driver.findElement(By.cssSelector("a.btn.check_out"));
            checkoutButton.click();
            currentURL = driver.getCurrentUrl();
            Assertions.assertEquals("https://automationexercise.com/checkout", currentURL);
            WebElement placeOrder = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Place Order")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", placeOrder);
            placeOrder.click();
            //Busqueda de elementos en el formulario de pago
            String urlActual = driver.getCurrentUrl();
            WebElement inputNameOnCard = driver.findElement(By.name("name_on_card"));
            WebElement inputCardNumber = driver.findElement(By.name("card_number"));
            WebElement inputCvc = driver.findElement(By.name("cvc"));
            WebElement inputExpiration = driver.findElement(By.name("expiry_month"));
            WebElement inputYear = driver.findElement(By.name("expiry_year"));
            WebElement payButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit")));
            //Accion sobre los elementos
            inputNameOnCard.sendKeys("Analia Bauer");
            inputCardNumber.sendKeys("4242 4242 4242 4242");
            inputCvc.sendKeys("123");
            inputExpiration.sendKeys("04");
            inputYear.sendKeys("2030");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payButton);
            payButton.click();

            driver.navigate().refresh();
            currentURL = driver.getCurrentUrl();
            Assertions.assertTrue(currentURL.contains("payment_done"), "URL Incorrecta");

            String textConfirmation = driver.findElement(By.cssSelector("#form > div > div > div > p")).getText();
            Assertions.assertEquals("Congratulations! Your order has been confirmed!", textConfirmation, "El texto no está presente");

        }

}
