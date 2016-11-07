package logaggregatorclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

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
                    generatePropertiesFile();
                }
            } catch (IOException e) {
                System.out.println("Client properties file exception: " + e);
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
            
            prop.setProperty(KEY_SERVER_URL, "");
            
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