/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author josanbir
 */
public class ManageApplicationsPage {
    
    private static WebElement element = null;

    public static WebElement Manage_applications(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[5]/div/div[3]/div"));

         return element;

         }
    
    public static WebElement entered_Manage_applications(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Create application')]"));

         return element;

         }
    
    public static WebElement Create_applications(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Create application')]"));

         return element;

         }
    
    public static WebElement entered_Create_applications_grid(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[3]/div/div"));

         return element;

         }
    
    public static WebElement Applications_name(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[3]/input"));

         return element;

         }
    
    
    public static WebElement Update_intervall(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[4]/input"));

         return element;

         }
    
    
    public static WebElement Create_application_button(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Create')]"));

         return element;

         }
    
    
    public static WebElement Create_applications_back_button(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Back')]"));

         return element;

         }
    
    
    public static WebElement Back_frm_manage_applications_page(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[4]/div/div[4]/div"));

         return element;

         }
    
}
