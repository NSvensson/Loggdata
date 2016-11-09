

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import javafx.stage.Stage;
import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;
import test_selenium.LoginFX;

public class LoginFXTest2 extends JavaFXTest {

    private JavaDriver driver;

    @Before public void setUp() {
        System.out.println("Hello im in setup of loginfxtest2");
        JavaProfile profile = new JavaProfile();
        profile.setLaunchType(LaunchType.FX_APPLICATION);
        driver = new JavaDriver(profile);
    }

    @Test public void loginEnabledWhenNameAndPasswordAreEntered() {
        System.out.println("Hello im in login enabled in when name and pass entered-lfxtest2)");
        PageLoginFX pl = new PageLoginFX(driver);
        pl.setUser("Joe");
        pl.setPassword("Burton");
        assertTrue(pl.getLogin().isEnabled());
    }

    @Test public void loginDisabledWhenNameIsEmpty() {
        PageLoginFX pl = new PageLoginFX(driver);
        pl.setUser("");
        pl.setPassword("Burton");
        assertFalse(pl.getLogin().isEnabled());
    }

    @Test public void loginDisabledWhenPasswordIsEmpty() {
        PageLoginFX pl = new PageLoginFX(driver);
        pl.setUser("Joe");
        pl.setPassword("");
        assertFalse(pl.getLogin().isEnabled());
    }

    @Test public void loginWithCredentials() {
        PageLoginFX pl = new PageLoginFX(driver);
        pl.login("Joe", "Burton");
        driver.switchTo().window("Login Success");
        WebElement messageLabel = driver.findElementByTagName("label");
        assertEquals("You logged in as: Joe with password: Burton", messageLabel.getText());
    }

    @Override public Stage getMainStage() {
        LoginFX loginFX = new LoginFX();
        return loginFX.getStage();
    }
}
