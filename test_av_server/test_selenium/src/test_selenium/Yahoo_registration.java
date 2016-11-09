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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

public class Yahoo_registration {

	public static void main(String[] args) throws InterruptedException {
// TODO Auto-generated method stub
// Initialize WebDriver
WebDriver driver;
System.setProperty("webdriver.gecko.driver", "src/test_selenium/geckodriver");
    driver = new FirefoxDriver();

// Maximize Window
  driver.manage().window().maximize();
  
// Wait For Page To Load
 driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

 //Navigate to Yahoo webstites
 driver.get("https://in.yahoo.com/?p=us// Mouse Over On ");
 
 Actions a1 = new Actions(driver);
 a1.moveToElement(driver.findElement(By.xpath("//emle='Sign In']"))).build().perform();
 Thread.sleep(3000L);
 System.out.println("clickedhe Sig-in button");
 
// Wait For Page To Load
 driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
 
 //Click on the Sign Up Button
 driver.findElement(By.xpath("//spanss='y-hdr-ln sign-up-btn']/a")).click();
 System.out.println("clickedhe Sig-Up button");
   
     
// so if a web drop down is getting developed using SELECT TAG in html the following code is implemented 
// -----------
// Select Date 
Select date =new Select(driver.findElement(By.id("day")));
 	
// Select date ["2"]  by Index  
// date.selectByIndex(2);
 
 
// Select date ["6"]  by Value        
date.selectByValue("6");	
//driver.findElement(By.id("day").getTextSystSystem.out.println("Dateh"));
   
// Select MONTH 
 Select month =new Select(driver.findElement(By.id("month")));

 // Select month ["MAY"]  by Index  
 month.selectByIndex(5);
 
 // Select month ["OCTOBER"]  by Value        
 month.selectByValue("10");
   
 // Select month ["January"]  by Visible Text	     
 month.selectByVisibleText("January");
     
 System.out.println("monthnuary");
     
 // --------------
     
// Select Year

Select year =new Select(driver.findElement(By.id("year")));
    
// Select year ["2012"]  by Index
  
year.selectByIndex(3);
System.out.println("Year13");
 

// -------------
// Select Gender
 driver.findElement(By.xpath("//*[@id='gender-wrapper']/fieldset/div/label[2]")).click();

 
 System.out.println("Gendermale");
   
 System.out.println("Theut of the Gender(Female) Is Enabled : " + driver.findElement(By.xpath("//*[@id='gender-wrapper']/fieldset/div/label[2]")).isEnabled());
 System.out.println("Theut of the Gender Is Displayed : " + driver.findElement(By.xpath("//*[@id='gender-wrapper']/fieldset/div/label[2]")).isDisplayed());
     
}
	    

}
