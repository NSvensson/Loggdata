package logaggregatorclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public final class Configurations {
    
    private static final String FILE_PATH = "LogClientConfig.properties";
    private static final String KEY_SERVER_URL = "Server_URL";
    
    public static String server_URL;
    
    public static void readPropertiesFile() {
        File properties_file = new File(FILE_PATH);
        
        if (properties_file.exists() && !properties_file.isDirectory()) {
            Properties properties = new Properties();
            FileInputStream properties_input_stream = null;
            
            try {
                properties_input_stream = new FileInputStream(properties_file);
                properties.load(properties_input_stream);
                
                if (!properties.isEmpty() &&
                    properties.containsKey(KEY_SERVER_URL)) {
                    
                    server_URL = properties.getProperty(KEY_SERVER_URL);
                } else {
                    // Properties key not found error handeling here
                }
            } catch (IOException e) {
                System.out.println("Client read properties file exception: " + e);
            } finally {
                if (properties_input_stream != null) {
                    try { properties_input_stream.close(); }
                    catch (IOException e) { System.out.println("Properties read properties file input stream close exception: " + e); }
                }
            }
        } else {
            // Properties file not found error handeling here
        }
    }
    
    public static void generatePropertiesFile(String server_url) {
        
        Properties prop = new Properties();
        OutputStream output = null;
        
        try {
            output = new FileOutputStream(FILE_PATH);
            
            if (server_url != null) prop.setProperty(KEY_SERVER_URL, server_url);
            else prop.setProperty(KEY_SERVER_URL, "");
            
            prop.store(output, null);
        } catch(IOException e) {
            System.out.println("Client generate properties file exception: " + e);
        } finally {
            if (output != null) {
                try { output.close(); }
                catch (IOException e) { System.out.println("Properties generate properties file input stream close exception: " + e); }
            }
        }
    }
    
    public static void generatePropertiesFile() {
        generatePropertiesFile(null);
    }
}