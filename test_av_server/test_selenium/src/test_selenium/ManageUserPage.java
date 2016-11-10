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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author josanbir
 */


import org.openqa.selenium.*;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

public class ManageUserPage {

    private static WebElement element = null;

    public static WebElement Managa_User(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[5]/div/div[1]/div"));

         return element;

         }

     public static WebElement Create_User(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Create user')]"));

         return element;

         }

     public static WebElement First_name(WebDriver driver){

         element = driver.findElement(By.cssSelector("#gwt-uid-9"));

         return element;

         }
     
     public static WebElement last_name(WebDriver driver){

         element = driver.findElement(By.cssSelector("#gwt-uid-11"));

         return element;

         }
     
     public static WebElement E_mail(WebDriver driver){

         element = driver.findElement(By.cssSelector("#gwt-uid-13"));

         return element;

         }
     
     public static WebElement User_name(WebDriver driver){

         element = driver.findElement(By.cssSelector("#gwt-uid-15"));

         return element;

         }
     
     
     public static WebElement Pass_word(WebDriver driver){

         element = driver.findElement(By.cssSelector("#gwt-uid-17"));

         return element;

         }
     
     public static WebElement ConfirmPass_word(WebDriver driver){

         element = driver.findElement(By.cssSelector("#gwt-uid-19"));

         return element;

         }
     
     public static WebElement Select_usergroup(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[1]/div/div[10]/div[2]/input"));

         return element;

         }
     
     public static WebElement Administrator(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]/table/tbody/tr[1]/td/span"));

         return element;

         }
     
     
     public static WebElement Regular_user(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]/table/tbody/tr[2]/td/span"));

         return element;

         }
     
     public static WebElement just_click_anywhere(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[1]/div"));

         return element;

         }
     
     
     
     public static WebElement Create(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[1]/div/div[11]/div/div[2]/div"));

         return element;

         }
     
     public static WebElement Back(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[1]/div/div[11]/div/div[3]/div"));

         return element;

         }
     
     public static WebElement Back_frm_userspage(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[4]/div/div[2]/div"));

         return element;

         }
     
     
     public static WebElement Logout(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Logout')]"));

         return element;

         }

}

