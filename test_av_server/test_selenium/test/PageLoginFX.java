//package net.sourceforge.marathon.demo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PageLoginFX {

    @FindBy(tagName = "text-field") WebElement userName;

    @FindBy(tagName = "password-field") WebElement password;
    @FindBy(tagName = "check-box") WebElement rememberMe;
    @FindBy(css = "button[text='Login']") WebElement login;
    @FindBy(css = "button[text='Cancel']") WebElement cancel;

    @SuppressWarnings("unused") private WebDriver driver;

    public PageLoginFX(WebDriver driver) {
        System.out.println("Hello im in pageloginFX");
        this.driver = driver;
        driver.switchTo().window("Login");
        PageFactory.initElements(driver, this);
    }

    public void login(String user, String pass) {
        System.out.println("Hello im in LOGIN OF pageloginfx");
        userName.sendKeys(user);
        password.sendKeys(pass);
        login.click();
    }

    public void setUser(String user) {
        System.out.println("Hello im in setuser of ploginfx");
        userName.clear();
        userName.sendKeys(user);
    }

    public void setPassword(String pass) {
        System.out.println("Hello im in setpassword of ploginfx");
        password.clear();
        password.sendKeys(pass);
    }

    public WebElement getLogin() {
        System.out.println("Hello im in WebElement getLogin");
        return login;
    }
}
