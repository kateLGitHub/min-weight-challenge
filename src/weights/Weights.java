package weights;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Algorithm: Given 9 bars, each of them has the same weight except one fake bar. It's weight has less weight. The task is to find fake bar.
 * Step 1: put 0,1,2 bars on the left side of scales; put 3,4,5 on the right side.
 * Step 2: get the updated result sign and make analyse: if result sign is "=" for left and right side then fake bar is in last 3 bars (6,7,8).
 * If left side > right side then take fake bar is in bars on the right side(3,4,5).
 * If left side < right side then take fake bar is in bars on the left side(0,1,2).
 * Step 3: there are three bars and it is needed to calculate the fake bar. Compare two bars to get result sign and make analyse:
 * if two bars are equal then third one is the fake bar.
 * If a bar on the left side > bar on the right side then bar on the right side is fake bar.
 * Otherwise, a bar on the left side is the fake bar.
 * Step 4: find number of fake bar and click it. Get window with the answer.
 */
public class Weights {

    private static final String EQUAL_SIGN = "=";
    private static final String GREATER_SIGN = ">";
    private static final String LESS_SIGN = "<";

    public static void main(String[] args) {

        // Open website to simulate user behavior.
        System.setProperty("webdriver.chrome.driver", System.getenv("CHROME_DRIVER"));
        ChromeDriver driver = new ChromeDriver();
        String baseURL = "http://ec2-54-208-152-154.compute-1.amazonaws.com/";
        driver.get(baseURL);
        // Find controls on the web page.
        WebElement leftCell0 = driver.findElement(By.id("left_0"));
        WebElement leftCell1 = driver.findElement(By.id("left_1"));
        WebElement leftCell2 = driver.findElement(By.id("left_2"));
        WebElement rightCell0 = driver.findElement(By.id("right_0"));
        WebElement rightCell1 = driver.findElement(By.id("right_1"));
        WebElement rightCell2 = driver.findElement(By.id("right_2"));
        WebElement weightButton = driver.findElement(By.id("weigh"));
        WebElement resultSignWebElement = driver.findElement(By.cssSelector("div.result button"));

        // Put 0,1,2 to the left, put 3,4,5 to the right.
        leftCell0.sendKeys("0");
        leftCell1.sendKeys("1");
        leftCell2.sendKeys("2");
        rightCell0.sendKeys("3");
        rightCell1.sendKeys("4");
        rightCell2.sendKeys("5");
        weightButton.click();

        // Wait updated result sign.
        // Criteria: log is updated with a new line.
        WebDriverWait wait = new WebDriverWait(driver, 20);
        boolean logUpdated = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div li:nth-child(1)"))) != null;
        if (!logUpdated) throw new RuntimeException("Timeout.");

        // Take result from the result sign.
        // Assumption: log and result sign are updated in the same time, no race condition check.
        String resultSign = resultSignWebElement.getText();
        System.out.println("(3 bars) left " + resultSign + " right");
        int ind;

        // Analyse result
        // Assumption: according result sign make decision which next 3 bars are needed to be analysed or throw exception.
        if (resultSign.equals(EQUAL_SIGN)) {
            ind = 6;
        } else if (resultSign.equals(GREATER_SIGN)) {
            ind = 3;
        } else if (resultSign.equals(LESS_SIGN)) {
            ind = 0;
        } else throw new RuntimeException("Unknown sign.");

        // Erase data from the previous run.
        // Assumption: erasing data with backspace key as users supposed to do, clearing inputs doesn't work because of javascript.
        leftCell0.sendKeys(Keys.BACK_SPACE);
        leftCell1.sendKeys(Keys.BACK_SPACE);
        leftCell2.sendKeys(Keys.BACK_SPACE);
        rightCell0.sendKeys(Keys.BACK_SPACE);
        rightCell1.sendKeys(Keys.BACK_SPACE);
        rightCell2.sendKeys(Keys.BACK_SPACE);

        // Put one bar on the left, put one bar on the right.
        leftCell0.sendKeys(String.valueOf(ind));
        rightCell0.sendKeys(String.valueOf(ind + 1));
        weightButton.click();

        // Wait updated result sign.
        // Assumption: log and result sign are updated at the same time.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div li:nth-child(2)")));
        resultSign = resultSignWebElement.getText();
        System.out.println("(1 bar) left " + resultSign + " right");

        // Analyse result and make decision which bar has minimum weight. Log is updated
        int min_ind;
        if (resultSign.equals(EQUAL_SIGN)) {
            min_ind = ind + 2;
        } else if (resultSign.equals(GREATER_SIGN)) {
            min_ind = ind + 1;
        } else {
            min_ind = ind;
        }
        System.out.println("Index of bar with min weight is: " + min_ind);

        // Click bar to check result.
        WebElement min_weight_bar = driver.findElement(By.cssSelector("div.coins button[id$='"+ min_ind +"']"));
        min_weight_bar.click();
//        driver.quit();
    }
}
