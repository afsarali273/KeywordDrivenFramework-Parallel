package com.k.DriverAction;

import com.k.utils.config.ConfigFileReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.time.Duration;

public class ActionKeywords {

    private WebDriver driver;

    public void openBrowser(String data) {
        System.out.println("Opening Browser");
        if (data.equals("Mozilla")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
            System.out.println("Mozilla browser started");
        } else if (data.equals("IE")) {
            driver = new InternetExplorerDriver();
            System.out.println("IE browser started");
        } else if (data.equals("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            System.out.println("Chrome browser started");
        }
        int implicitWaitTime = ConfigFileReader.getInstance().readConfig().getMaxTimeout();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitTime));
    }

    public void navigate(String url) {

        System.out.println("Navigating to URL");
        driver.get(url);

    }

    public void click(String locator) {

        System.out.println("Clicking on Webelement ");
        driver.findElement(getLocator(locator)).click();
    }

    public void input(String locator, String data) {

        System.out.println("Entering the text ");
        driver.findElement(getLocator(locator)).sendKeys(data);

    }


    public void waitFor(String data) {
        System.out.println("Wait for 5 seconds");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void closeBrowser() {
        System.out.println("Closing the browser");
        driver.quit();
    }

    public void isDisplayed(String locator) {

        System.out.println("Is Element Displayed ? ");
        boolean isDisplayed = driver.findElement(getLocator(locator)).isDisplayed();
        System.out.println("Element is Displayed ? - " + isDisplayed);

    }

    private By getLocator(String locatorfromIniFile) {

        if (StringUtils.isEmpty(locatorfromIniFile))
            return null;
        String locatorValue = locatorfromIniFile.split("::")[1];
        switch (locatorfromIniFile.split("::")[0].toLowerCase()) {
            case "xpath":
                return new By.ByXPath(locatorValue);
            case "classname":
                return new By.ByClassName(locatorValue);
            case "cssselector":
                return new By.ByCssSelector(locatorValue);
            case "id":
                return new By.ById(locatorValue);
            case "partiallinktext":
                return new By.ByPartialLinkText(locatorValue);
            case "linktext":
                return new By.ByLinkText(locatorValue);
            case "tagname":
                return new By.ByTagName(locatorValue);
        }
        return null;
    }

}