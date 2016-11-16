/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_selenium;

import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author josanbir
 */
public class GUItwoJUnitTest {
    private static WebDriver driver = null;
    
    public GUItwoJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
        
        System.setProperty("webdriver.gecko.driver", "src/test_selenium/geckodriver");
        driver = new FirefoxDriver();
// Wait For Page To Load

        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
// Go to URL

        driver.get("http://localhost:8080/LogAggregatorServer/");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
    }
    
    @After
    public void tearDown() {
        System.out.println("To quit the browser");

    driver.quit();
        
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
     
     @Test
     public void TestB() {
         Login_page.txtbx_UserName(driver).sendKeys("demon");

     Login_page.txtbx_Password(driver).sendKeys("admin");

     Login_page.btn_LogIn(driver).click();
     
     driver.manage().timeouts().implicitlyWait(600, TimeUnit.SECONDS);
     
     String verify_text2 = driver.findElement(By.xpath("//h1[contains(text(), 'Invalid credentials provided.')]")).getText();
     driver.manage().timeouts().implicitlyWait(600, TimeUnit.SECONDS);
     Assert.assertTrue(verify_text2.toLowerCase().contains("invalid credentials provided"));
     System.out.println(" Login unSuccessfull" +  verify_text2);
     }
}
