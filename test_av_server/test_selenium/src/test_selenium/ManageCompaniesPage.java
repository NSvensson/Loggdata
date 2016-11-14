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
public class ManageCompaniesPage {
    
    private static WebElement element = null;

    public static WebElement Manage_Companies(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[5]/div/div[2]/div"));

         return element;

         }
    
    public static WebElement entered_Manage_Companies(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Create company')]"));

         return element;

         }
    
    
    public static WebElement Create_Company(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Create company')]"));

         return element;

         }
    
    public static WebElement entered_create_Company_grid(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[3]/div/div"));

         return element;

         } 
    
    public static WebElement Enter_Company_name(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[3]/input"));

         return element;

         } 
    
    
    public static WebElement Enter_Website (WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[4]/input"));

         return element;

         } 
    
    
   public static WebElement Enter_Details(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[5]/input"));

         return element;

         } 
   
   
   public static WebElement Click_create(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[6]/div/div/div[1]/div"));

         return element;

         } 
   
   
   public static WebElement Click_back(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[6]/div/div/div[2]/div"));

         return element;

         } 
   
   public static WebElement Click_back_again(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[4]/div/div[4]/div"));

         return element;

         } 
    //----------------------------------------edit company locators--------------------------
   
   public static WebElement select_company_to_edit(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[3]/div/div[3]/table/tbody/tr[1]/td[1]"));

         return element;

         } 
   
   public static WebElement Click_edit_company(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[4]/div/div[2]/div"));

         return element;

         } 
   
   public static WebElement entered_edit_company(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[3]/div/div"));

         return element;

         } 
   
   
   public static WebElement Click_to_edit_company_name(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[3]/input"));

         return element;

         } 
   
   
   public static WebElement Click_to_edit_website(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[4]/input"));

         return element;

         } 
   
   
   public static WebElement Click_to_edit_details(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[5]/input"));

         return element;

         } 
   
   
   public static WebElement Click_done(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[6]/div/div/div[1]/div"));

         return element;

         } 
   
   
   public static WebElement Click_back_edit(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div/div/div[6]/div/div/div[2]/div"));

         return element;

         } 
   
   
//-------------------------------------Delete company locators-----------------------------------
   
   public static WebElement select_company_to_delete(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[3]/div/div[3]/table/tbody/tr[1]/td[1]"));

         return element;

         } 
   
   
   public static WebElement Click_delete_company(WebDriver driver){

         element = driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div[2]/div/div[4]/div/div[3]/div"));

         return element;

         } 
   
   
   public static WebElement Click_yes(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Yes')]"));

         return element;

         } 
   
   
   public static WebElement Click_confirm_yes(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'Yes')]"));

         return element;

         } 
   
   
   public static WebElement Click_NO(WebDriver driver){

         element = driver.findElement(By.xpath("//span[contains(text(), 'No')]"));

         return element;

         } 
   
   
   
}
