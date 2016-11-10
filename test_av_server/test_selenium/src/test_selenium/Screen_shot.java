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
//import com.sun.jna.platform.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;

public class Screen_shot {

public static void main(String[] args) throws IOException {
// TODO Auto-generated method stub

// Initialize WebDriver
WebDriver driver;
System.setProperty("webdriver.gecko.driver", "src/test_selenium/geckodriver");
        driver = new FirefoxDriver();
// Wait For Page To Load

        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
// Go to URL

        driver.get("http://localhost:8080/LogAggregatorServer/");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
        //WebElement loginButton = driver.findElement(By.cssSelector("input.Login"));
        driver.findElement(By.tagName("input")).sendKeys("admin");
        
        driver.findElement(By.cssSelector("#gwt-uid-5")).sendKeys("admin");
        driver.findElement(By.cssSelector("div.v-button.v-widget")).click();
        
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.SECONDS);
       
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[5]/div/div[1]/div")).click();
        
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
        
        driver.findElement(By.xpath("//span[contains(text(), 'Create user')]")).click();
        
        //driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[4]/div/div[3]/div")).click(); //edit user
        
        //driver.findElement(By.name("button")).click();
        
        
        //
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.cssSelector("#gwt-uid-9")).sendKeys("ha");
        driver.findElement(By.cssSelector("#gwt-uid-11")).sendKeys("llo");
        driver.findElement(By.cssSelector("#gwt-uid-13")).sendKeys("hallo@gmail.com");
        driver.findElement(By.cssSelector("#gwt-uid-15")).sendKeys("hallo");
        driver.findElement(By.cssSelector("#gwt-uid-17")).sendKeys("hallo");
        driver.findElement(By.cssSelector("#gwt-uid-19")).sendKeys("hallo");
        
        
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[1]/div/div[10]/div[2]/input")).click();
        
        driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]/table/tbody/tr[1]/td/span")).click(); // administor
        //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]/table/tbody/tr[2]/td/span")).click(); // regular user
        
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
      
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[1]/div")).click(); //just click anywhere
        
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[1]/div/div[11]/div/div[2]/div")).click();
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[1]/div/div[11]/div/div[3]/div")).click();
        

        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
        
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[4]/div/div[2]/div")).click(); //back button

        
        
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        
        driver.findElement(By.xpath("//span[contains(text(), 'Logout')]")).click();
        //SeleniumUtil.fluentWait(By.name("handle"), driver);
        //WebElement pass = driver.findElement(By.tagName("password:"));
        //pass.sendKeys("admin");
        //pass.submit();
        
        //WebElement button = driver.findElement(By.id("LOGIN"));
        //button.click();

        
        
        
       
//driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
//WebDriverWait wait = new WebDriverWait(driver,30);
//wait.until(ExpectedConditions.elementToBeClickable(element));
//driver.findElement(By.name("Username")).sendKeys("admin");
//driver.findElement(By.id("Next")).click();
// Enter Password
//driver.findElement(By.name("Password")).sendKeys("admin");
// Wait For Page To Load
//driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
// Click on 'Sign In' button
//driver.findElement(By.id("signIn")).click();
//Click on Compose Mail.
//driver.findElement(By.name("Password")).submit();
//driver.findElement(By.name("Login")).click();
// Click on the image icon present in the top right navigational Bar
//driver.findElement(By.xpath("//div[@class='gb_1 gb_3a gb_nc gb_e']/div/a")).click();
//Click on 'Logout' Button
//driver.findElement(By.xpath("//*[@id='gb_71']")).click();
//Close the browser.*/
// Close Driver
//driver.quit();
}
}