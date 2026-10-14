package com.diaglab.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PortalSmokeTests {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String BASE_URL = System.getProperty("baseUrl",
            System.getenv().getOrDefault("BASE_URL", "http://localhost:5173"));
    private static final String USERNAME = System.getProperty("username",
            System.getenv().getOrDefault("PORTAL_USERNAME", "admin"));
    private static final String PASSWORD = System.getProperty("password",
            System.getenv().getOrDefault("PORTAL_PASSWORD", ""));

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,1000", "--disable-notifications");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        driver.get(BASE_URL);
    }

    @AfterEach
    void captureFailureAndClose(TestInfo testInfo) {
        try {
            // Each test captures a screenshot immediately before rethrowing a failure.
        } catch (Exception ignored) {
        } finally {
            if (driver != null) driver.quit();
        }
    }

    @Test
    @Order(1)
    void validLoginOpensDashboard() {
        assertFalse(PASSWORD.isBlank(),
                "Set PORTAL_PASSWORD or pass -Dpassword=... before running the suite.");
        try {
            login();
            wait.until(d -> {
                String text = d.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
                return text.contains("dashboard") || text.contains("patients") || text.contains("welcome");
            });
            String body = driver.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
            assertTrue(body.contains("dashboard") || body.contains("patients") || body.contains("welcome"),
                    "Expected the authenticated dashboard or portal navigation to be visible.");
        } catch (Throwable t) {
            saveScreenshot("TC-01-login-dashboard");
            throw t;
        }
    }

    @Test
    @Order(2)
    void patientCanBeCreatedAndFound() {
        assertFalse(PASSWORD.isBlank(),
                "Set PORTAL_PASSWORD or pass -Dpassword=... before running the suite.");
        try {
            login();
            clickNavigation("patients");
            clickFirstMatching(List.of("add patient"));
            String unique = String.valueOf(System.currentTimeMillis()).substring(7);
            String patientName = "Selenium Patient " + unique;
            fillField(List.of("patientcode"), "SEL-" + unique);
            fillField(List.of("fullname"), patientName);
            fillField(List.of("gender"), "Other", true);
            fillField(List.of("phone"), "900000" + String.format("%04d", Integer.parseInt(unique) % 10000));
            fillField(List.of("email"), "selenium" + unique + "@example.com");
            fillField(List.of("address"), "Selenium Test Address", false);
            clickFirstMatching(List.of("save record"));
            WebElement search = driver.findElement(By.cssSelector("input[placeholder='Search patients…']"));
            search.sendKeys(patientName);
            wait.until(d -> d.findElement(By.tagName("body")).getText().contains(patientName));
            assertTrue(driver.findElement(By.tagName("body")).getText().contains(patientName),
                    "Newly created patient should appear in the patient list.");
        } catch (Throwable t) {
            saveScreenshot("TC-02-patient-registration");
            throw t;
        }
    }

    @Test
    @Order(3)
    void labTestStatusCanBeUpdated() {
        assertFalse(PASSWORD.isBlank(),
                "Set PORTAL_PASSWORD or pass -Dpassword=... before running the suite.");
        try {
            login();
            clickNavigation("lab tests");
            WebElement status = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("select.small-select")));
            Select statusSelect = new Select(status);
            String currentValue = statusSelect.getFirstSelectedOption().getAttribute("value");
            String targetValue = !"IN_PROGRESS".equals(currentValue) ? "IN_PROGRESS" : "COMPLETED";
            statusSelect.selectByValue(targetValue);

            // The portal updates the status asynchronously through its backend.
            // Wait for the application's success/error notification rather than
            // checking the select immediately, because the select is controlled by React.
            wait.until(d -> {
                String text = d.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
                return text.contains("test status updated.")
                        || text.contains("failed")
                        || text.contains("error")
                        || text.contains("could not")
                        || text.contains("unauthorized")
                        || text.contains("forbidden");
            });
            String body = driver.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
            assertTrue(body.contains("test status updated."),
                    "Lab test status update did not succeed. Portal message: " + body);
        } catch (Throwable t) {
            saveScreenshot("TC-03-lab-test-status");
            throw t;
        }
    }

    private void login() {
        // The app opens on a public landing page; open the login form first.
        WebElement signIn = findClickable("sign in");
        if (signIn == null) {
            signIn = findClickable("access your portal");
        }
        if (signIn == null) {
            signIn = findClickable("sign in to diaglab");
        }
        if (signIn != null) {
            signIn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[autocomplete='username']")));
        }

        WebElement username = driver.findElement(By.cssSelector("input[autocomplete='username']"));
        WebElement password = driver.findElement(By.cssSelector("input[autocomplete='current-password']"));
        username.clear();
        username.sendKeys(USERNAME);
        password.clear();
        password.sendKeys(PASSWORD);

        WebElement submit = driver.findElement(By.cssSelector("form.login-card button.btn.primary"));
        submit.click();

        wait.until(d -> {
            String body = d.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
            return body.contains("good to see you") || body.contains("unable to sign in")
                    || body.contains("invalid") || body.contains("dashboard");
        });
        String body = driver.findElement(By.tagName("body")).getText().toLowerCase(Locale.ROOT);
        assertFalse(body.contains("unable to sign in") || body.contains("invalid username")
                        || body.contains("invalid credentials"),
                "Login was rejected; check the supplied test account.");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".sidebar")));
    }

    private void clickNavigation(String phrase) {
        WebElement element = findClickable(phrase);
        assertNotNull(element, "Could not find navigation control containing: " + phrase);
        element.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
    }

    private void clickFirstMatching(List<String> phrases) {
        for (String phrase : phrases) {
            WebElement e = findClickable(phrase);
            if (e != null) {
                e.click();
                return;
            }
        }
        fail("Could not find a clickable button/link matching: " + phrases);
    }

    private WebElement findClickable(String phrase) {
        String p = phrase.toLowerCase(Locale.ROOT);
        for (WebElement e : driver.findElements(By.cssSelector("button, a, [role='button']"))) {
            try {
                String text = (e.getText() + " " + e.getAttribute("aria-label")).toLowerCase(Locale.ROOT);
                if (text.contains(p) && e.isDisplayed() && e.isEnabled()) return e;
            } catch (StaleElementReferenceException ignored) {}
        }
        return null;
    }

    private WebElement findInput(List<String> terms) {
        for (WebElement e : driver.findElements(By.cssSelector("input, textarea, select"))) {
            String attrs = ((e.getAttribute("name") == null ? "" : e.getAttribute("name")) + " " +
                    (e.getAttribute("placeholder") == null ? "" : e.getAttribute("placeholder")) + " " +
                    (e.getAttribute("aria-label") == null ? "" : e.getAttribute("aria-label")) + " " +
                    (e.getAttribute("type") == null ? "" : e.getAttribute("type"))).toLowerCase(Locale.ROOT);
            for (String term : terms) {
                if (attrs.contains(term) && e.isDisplayed()) return e;
            }
        }
        for (WebElement label : driver.findElements(By.tagName("label"))) {
            String labelText = label.getText().toLowerCase(Locale.ROOT);
            for (String term : terms) {
                if (labelText.contains(term)) {
                    String id = label.getAttribute("for");
                    if (id != null && !id.isBlank()) {
                        List<WebElement> found = driver.findElements(By.id(id));
                        if (!found.isEmpty()) return found.get(0);
                    }
                    try {
                        WebElement nested = label.findElement(By.cssSelector("input, textarea, select"));
                        if (nested.isDisplayed()) return nested;
                    } catch (NoSuchElementException ignored) {}
                }
            }
        }
        if (terms.contains("username") || terms.contains("email")
                || terms.contains("user name") || terms.contains("login")) {
            for (WebElement e : driver.findElements(
                    By.cssSelector("input[type='text'], input[type='email']"))) {
                if (e.isDisplayed() && e.isEnabled()) {
                    return e;
                }
            }
        }
        return null;
    }

    private void fillField(List<String> terms, String value) { fillField(terms, value, false); }

    private void fillField(List<String> terms, String value, boolean selectOption) {
        WebElement e = findInput(terms);
        if (e == null) {
            if (terms.contains("age") || terms.contains("phone") || terms.contains("mobile")
                    || terms.contains("contact") || terms.contains("gender") || terms.contains("address")) return;
            fail("Could not find form field matching: " + terms);
        }
        if (e.getTagName().equalsIgnoreCase("select")) {
            for (WebElement option : e.findElements(By.tagName("option"))) {
                if (option.getText().equalsIgnoreCase(value) ||
                        option.getText().toLowerCase(Locale.ROOT).contains(value.toLowerCase(Locale.ROOT))) {
                    option.click();
                    return;
                }
            }
            if (selectOption) return;
        } else {
            e.clear();
            e.sendKeys(value);
        }
    }

    private WebElement findStatusControl() {
        for (WebElement e : driver.findElements(By.cssSelector("select, input, button"))) {
            String attrs = ((e.getAttribute("name") == null ? "" : e.getAttribute("name")) + " " +
                    (e.getAttribute("aria-label") == null ? "" : e.getAttribute("aria-label")) + " " +
                    (e.getAttribute("placeholder") == null ? "" : e.getAttribute("placeholder")) + " " +
                    e.getText()).toLowerCase(Locale.ROOT);
            if (attrs.contains("status") && e.isDisplayed() && e.isEnabled()) return e;
        }
        return null;
    }

    private void saveScreenshot(String testName) {
        if (driver == null) return;
        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            String safeName = testName.replaceAll("[^a-zA-Z0-9._-]", "_");
            byte[] image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(dir.resolve(safeName + ".png"), image);
            System.err.println("Screenshot saved: " + dir.resolve(safeName + ".png").toAbsolutePath());
        } catch (IOException | WebDriverException e) {
            System.err.println("Could not save failure screenshot: " + e.getMessage());
        }
    }
}
