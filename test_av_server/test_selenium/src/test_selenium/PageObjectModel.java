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

     import org.openqa.selenium.WebDriver;

     import org.openqa.selenium.firefox.FirefoxDriver;

     // Import package pageObject.*

     

     import test_selenium.Login_page;

public class PageObjectModel {

     private static WebDriver driver = null;

   public static void main(String[] args) {

     //WebDriver driver;
System.setProperty("webdriver.gecko.driver", "src/test_selenium/geckodriver");
        driver = new FirefoxDriver();
// Wait For Page To Load

        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
// Go to URL

        driver.get("http://localhost:8080/LogAggregatorServer/");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);

     // Use page Object library now

     

     Login_page.txtbx_UserName(driver).sendKeys("admin");

     Login_page.txtbx_Password(driver).sendKeys("admin");

     Login_page.btn_LogIn(driver).click();

     System.out.println(" Login Successfully");
     
     
     ManageUserPage.Managa_User(driver).click();
     ManageUserPage.Create_User(driver).click();
     ManageUserPage.First_name(driver).sendKeys("chu");
     ManageUserPage.last_name(driver).sendKeys("chu");
     ManageUserPage.E_mail(driver).sendKeys("chuchu@gmail");
     ManageUserPage.User_name(driver).sendKeys("chuchu");
     ManageUserPage.Pass_word(driver).sendKeys("chuchu");
     ManageUserPage.ConfirmPass_word(driver).sendKeys("chuchu");
     ManageUserPage.Select_usergroup(driver).click();
     //ManageUserPage.Administrator(driver).click();
     ManageUserPage.Regular_user(driver).click();
     ManageUserPage.just_click_anywhere(driver).click();
     ManageUserPage.Create(driver).click();
     ManageUserPage.Back(driver).click();
     
     ManageUserPage.Back_frm_userspage(driver).click();
     ManageUserPage.Logout(driver).click();
     
     //Home_Page.lnk_LogOut(driver).click(); 
     
     System.out.println(" Logout Successfully");

     //driver.quit();

     }

}
