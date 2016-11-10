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


import org.openqa.selenium.*;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

public class Login_page {

    private static WebElement element = null;

    public static WebElement txtbx_UserName(WebDriver driver){

         element = driver.findElement(By.tagName("input"));

         return element;

         }

     public static WebElement txtbx_Password(WebDriver driver){

         element = driver.findElement(By.cssSelector("#gwt-uid-5"));

         return element;

         }

     public static WebElement btn_LogIn(WebDriver driver){

         element = driver.findElement(By.cssSelector("div.v-button.v-widget"));

         return element;

         }

}
