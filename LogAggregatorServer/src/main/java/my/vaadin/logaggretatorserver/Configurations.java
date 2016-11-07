package my.vaadin.logaggretatorserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public final class Configurations {
    
    private static final String FILE_PATH = "LogServerConfig.properties";
    
    private static final String KEY_DATABASE_URI = "Database_URI";
    private static final String KEY_DATABASE_USERNAME = "Database_username";
    private static final String KEY_DATABASE_PASSWORD = "Database_password";
    
    public static String database_URI;
    public static String database_username;
    public static String database_password;
    
    public static void readPropertiesFile() {
        File properties_file = new File(FILE_PATH);
        
        if (properties_file.exists() && !properties_file.isDirectory()) {
            Properties properties = new Properties();
            FileInputStream properties_input_stream = null;
            
            try {
                properties_input_stream = new FileInputStream(properties_file);
                properties.load(properties_input_stream);
                
                if (!properties.isEmpty() &&
                    properties.containsKey(KEY_DATABASE_URI) &&
                    properties.containsKey(KEY_DATABASE_USERNAME) &&
                    properties.containsKey(KEY_DATABASE_PASSWORD)) {
                    
                    database_URI = properties.getProperty(KEY_DATABASE_URI);
                    database_username = properties.getProperty(KEY_DATABASE_USERNAME);
                    database_password = properties.getProperty(KEY_DATABASE_PASSWORD);
                } else {
                    generatePropertiesFile();
                }
            } catch (IOException e) {
                System.out.println("Server properties file exception: " + e);
            } finally {
                if (properties_input_stream != null) {
                    try { properties_input_stream.close(); }
                    catch (IOException e) { System.out.println("Properties input stream close exception: " + e); }
                }
            }
        } else {
            generatePropertiesFile();
        }
    }
    
    private static void generatePropertiesFile(){
        
        Properties prop = new Properties();
        OutputStream output = null;
        
        try {
            output = new FileOutputStream(FILE_PATH);
            
            prop.setProperty(KEY_DATABASE_URI, "");
            prop.setProperty(KEY_DATABASE_USERNAME, "");
            prop.setProperty(KEY_DATABASE_PASSWORD, "");
            
            prop.store(output, null);
        } catch(IOException e) {
            System.out.println(e);
        } finally {
            if (output != null) {
                try { output.close(); }
                catch (IOException e) { System.out.println(e); }
            }
        }
    } 
}