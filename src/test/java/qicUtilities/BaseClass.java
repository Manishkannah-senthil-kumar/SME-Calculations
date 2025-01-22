package qicUtilities;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

//import dev.failsafe.internal.util.Durations;


public class BaseClass {
	public static WebDriver driver;

    


    @BeforeMethod
    public void PreCondition(String browser){

        if (browser.equalsIgnoreCase("chrome")){
           
            driver = new ChromeDriver();
        }

        if (browser.equalsIgnoreCase("edge")) {

            driver = new EdgeDriver();
            
        } 
        else {
            driver = new FirefoxDriver();
            
        }    
        
        driver.get("https://smehealth-uat.aurainsure.tech/04/qic/login");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));


    }

    @AfterMethod
    public void Quit(){

        driver.quit();
    }


    

       

    

	   
	   
   }
    


