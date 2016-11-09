/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_selenium;

/**
 *
 * @author josanbir
 */
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

public class Right_click {

public static void main(String args[]) throws Exception{

       // Initialize WebDriver
       WebDriver driver;
       System.setProperty("webdriver.gecko.driver", "src/test_selenium/geckodriver");
    driver = new FirefoxDriver();
       // Wait For Page To Load
       driver.manage().timeouts().implicitlyWait(120,TimeUnit.SECONDS);
   
       // Go to Myntra Page 
        driver.get("http://www.myntra.com/");     
       // Maximize Window
       driver.manage().window().maximize();
      
      WebElement R1 = driver.findElement(By.xpath("//af='Men?src=tn&nav_id=20']"));
      
      // Initialize Actions class object
      Actions builder = new Actions(driver);
      
      // Perform Right Click on  MEN and  Open "Men" content in a new tab 
      builder.contextClick(R1).sendKeys(Keys.ENTER).perform();
      //ContextClick() is a method to use right click 
    
       //Perform Right Click on  MEN and  Open "Men" content in a new different Window
      
       //builder.contextClick(Download App).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).perform();
  
      //closing current driver window 
      //driver.close();
     
		
	}

}
