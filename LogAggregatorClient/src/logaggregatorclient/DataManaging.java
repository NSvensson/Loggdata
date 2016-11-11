package logaggregatorclient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class DataManaging {

    private final String FILE_PATH = "config.properties";
    
    public final String API_KEY = "api_key";
    public final String UPDATE_INTERVAL = "update_interval";
    public final String LINE_ONE = "line_one";
    public final String LINE_TWO = "line_two";
    public final String LINE_THREE = "line_three";
    public final String LOG_URI = "log_uri";
    
    public void generatePropertiesFile(String log_uri, String api_key, String update_interval, String line_one, String line_two, String line_three){
        
        Properties prop = new Properties();
        OutputStream output = null;
        
        try {
            output = new FileOutputStream(this.FILE_PATH);
            
            prop.setProperty(this.LOG_URI, log_uri);
            prop.setProperty(this.API_KEY, api_key);
            prop.setProperty(this.UPDATE_INTERVAL, update_interval);
            
            if (line_one != null) prop.setProperty(this.LINE_ONE, line_one);
            else prop.setProperty(this.LINE_ONE, "");
            
            if (line_two != null) prop.setProperty(this.LINE_TWO, line_two);
            else prop.setProperty(this.LINE_TWO, "");
            
            if (line_three != null) prop.setProperty(this.LINE_THREE, line_three);
            else prop.setProperty(this.LINE_THREE, "");
            
            prop.store(output, null);
        } catch(IOException e){
            System.out.println(e);
        }finally{
        if(output != null){
            try{
                output.close();
                }catch(IOException e){
                    System.out.println(e);
                }
            }
        }
    } 
        
    public HashMap<String, String> readPropertiesFile() {
        
        HashMap<String, String> results = null;
        
        Properties prop = new Properties();
        InputStream input = null;
        
        try{
            input = new FileInputStream(this.FILE_PATH);
            prop.load(input);
            
            results = new HashMap<String, String>();
            
            // puts the collected data in to the Hashmap object
            
            results.put(LOG_URI,prop.getProperty(LOG_URI));
            results.put(this.API_KEY, prop.getProperty(this.API_KEY));
            results.put(this.UPDATE_INTERVAL, prop.getProperty(this.UPDATE_INTERVAL));
            results.put(this.LINE_ONE, prop.getProperty(this.LINE_ONE));
            results.put(this.LINE_TWO, prop.getProperty(this.LINE_TWO));
            results.put(this.LINE_THREE, prop.getProperty(this.LINE_THREE));
            
        }catch(IOException e) {
            System.out.println("Error : "+e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch(IOException ex) {
                    System.out.println("Error : "+ex);
                }
            }
        }
        
        return results;
    }
    
    public static String hashString(String received_string) {
        
        return Integer.toString(received_string.hashCode());
    }
}