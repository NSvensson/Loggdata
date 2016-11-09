package test_selenium;

import javafx.application.Application;
import javafx.stage.Stage;

public class LoginFXLaunch extends Application {

    @Override public void start(Stage primaryStage) throws Exception {
        System.out.println("Hello im in START of loginfxLAUNCH");
        LoginFX loginFX = new LoginFX();
        primaryStage = loginFX.getStage();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
