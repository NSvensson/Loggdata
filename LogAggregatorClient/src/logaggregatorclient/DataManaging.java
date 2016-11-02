package logaggregatorclient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class DataManaging {

    private final String FILE_PATH = "config.properties";
    
    private final String API_KEY = "api_key";
    private final String UPDATE_INTERVAL = "update_interval";
    private final String LINE_ONE = "line_one";
    private final String LINE_TWO = "line_two";
    private final String LINE_THREE = "line_three";
    
    
    
    public void generatePropertiesFile(String api_key, String update_interval, String line_one, String line_two, String line_three){
        
        Properties prop = new Properties();
        OutputStream output = null;
        
        try{
            output = new FileOutputStream(this.FILE_PATH);
            
            prop.setProperty(this.API_KEY, api_key);
            prop.setProperty(this.UPDATE_INTERVAL, update_interval);
            prop.setProperty(this.LINE_ONE, line_one);
            prop.setProperty(this.LINE_TWO, line_two);
            prop.setProperty(this.LINE_THREE, line_three);
            
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
        
    public String[] readPropertiesFile() {
        
        String[] results = null;
        
        Properties prop = new Properties();
        InputStream input = null;
        
        try{
            input = new FileInputStream(this.FILE_PATH);
            prop.load(input);
            
            results = new String[5];
            
            // print out the collected data
            System.out.println(prop.getProperty(API_KEY));
            results[0] = prop.getProperty(this.API_KEY);
            
            System.out.println(prop.getProperty(UPDATE_INTERVAL));
            results[1] = prop.getProperty(this.UPDATE_INTERVAL);
            
            System.out.println(prop.getProperty(LINE_ONE));
            results[2] = prop.getProperty(this.LINE_ONE);
            
            System.out.println(prop.getProperty(LINE_TWO));
            results[3] = prop.getProperty(this.LINE_TWO);
            
            System.out.println(prop.getProperty(LINE_THREE));
            results[4] = prop.getProperty(this.LINE_THREE);
            
        }catch(IOException e){
            System.out.println("Error : "+e);
        } finally{
            if(input != null){
                try{
                input.close();
                    }catch(IOException ex){
                    System.out.println("Error : "+ex);
                        }
                }
        }
        
        return results;
    }
}
