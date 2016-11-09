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
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
 
public class Login_demo extends Application {
 
   @Override
   public void start(Stage stage) {
       Group root = new Group();
       Scene scene = new Scene(root, 500, 500, Color.BLACK);
       stage.setTitle("JavaFX Scene Graph Demo");
       stage.setScene(scene);
       stage.show();
   }
 
   public static void main(String[] args) {
       launch(args);
   }
}