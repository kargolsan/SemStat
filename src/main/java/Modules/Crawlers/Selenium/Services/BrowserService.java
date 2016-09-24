package Modules.Crawlers.Selenium.Services;

import org.openqa.selenium.WebDriver;
import Application.Services.PropertyService;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by IntelliJ IDEA.
 * User: Karol Golec
 * Date: 23.09.2016
 * Time: 16:30
 */
public class BrowserService {

    /** @var driver */
    WebDriver driver;

    /**
     * Get driver
     *
     * @return driver
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Constructor
     */
    public BrowserService(){
        System.setProperty("webdriver.chrome.driver", "drivers\\chromedriver.exe");
        this.driver = new ChromeDriver();
    }

    /**
     * Run browser
     */
    public void run(){
        this.driver.get(PropertyService.get("url", "Application/Resources/properties.properties"));
    }
}
