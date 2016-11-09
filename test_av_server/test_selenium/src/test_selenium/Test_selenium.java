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
import static com.google.common.base.Joiner.on;
import static com.google.gson.stream.JsonToken.NAME;
import static com.sun.javafx.fxml.expression.Expression.set;
import static com.sun.javafx.fxml.expression.Expression.set;
import static com.sun.scenario.Settings.set;
import static com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.set;
import static java.lang.reflect.Array.set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
public class Test_selenium {
public static void main(String[] args) {
// Create a new instance of the Firefox driver
WebDriver driver;
System.setProperty("webdriver.gecko.driver", "src/test_selenium/geckodriver");
    driver = new FirefoxDriver();
//  Wait For Page To Load
// Put a Implicit wait, this means that any search for elements on the page
//could take the time the implicit wait is set for before throwing exception 
driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
// Navigate to URL
driver.get("https://mail.yahoo.com/");
// Maximize the window.
driver.manage().window().maximize();
// Enter UserName
driver.findElement(By.id("sign in")).sendKeys("username");
driver.findElement(By.id("Next")).click();
// Enter Password
driver.findElement(By.id("Passwd")).sendKeys("password");
// Wait For Page To Load
driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
// Click on 'Sign In' button
//driver.findElement(By.id("signIn")).click();
//Click on Compose Mail.
driver.findElement(By.xpath("//div[@class='z0']/div")).click();
// Click on the image icon present in the top right navigational Bar
driver.findElement(By.xpath("//div[@class='gb_1 gb_3a gb_nc gb_e']/div/a")).click();
//Click on 'Logout' Button
driver.findElement(By.xpath("//*[@id='gb_71']")).click();
//Close the browser.
driver.close();
}
}
