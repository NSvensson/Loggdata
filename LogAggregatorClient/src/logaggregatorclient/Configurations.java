package logaggregatorclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public final class Configurations {
    
    public static final String APPLICATION_SEPERATOR = ",";
    
    private static final String APPLICATION_CONFIGURATIONS_FILE_PATH = "AppConfigs.properties";
    private static final String CLIENT_CONFIGURATIONS_FILE_PATH = "LogClientConfig.properties";
    private static final String KEY_APPLICATION_IDS = "Application_IDs";
    private static final String KEY_SERVER_URL = "Server_URL";
    
    public static boolean client_configurations_found = false;
    
    public static String[] application_IDs;
    
    public static String server_URL;
    
    public static void readPropertiesFile() {
        File properties_file = new File(CLIENT_CONFIGURATIONS_FILE_PATH);
        
        if (properties_file.exists() && !properties_file.isDirectory()) {
            Properties properties = new Properties();
            FileInputStream properties_input_stream = null;
            
            client_configurations_found = true;
            
            try {
                properties_input_stream = new FileInputStream(properties_file);
                properties.load(properties_input_stream);
                
                if (!properties.isEmpty() &&
                    properties.containsKey(KEY_SERVER_URL) &&
                    properties.containsKey(KEY_APPLICATION_IDS)) {
                    
                    server_URL = properties.getProperty(KEY_SERVER_URL);
                    
                    String tmpApplicationValues;
                    if ((tmpApplicationValues = properties.getProperty(KEY_APPLICATION_IDS, null)) != null) {
                        if (tmpApplicationValues.contains(APPLICATION_SEPERATOR)) application_IDs = tmpApplicationValues.split(APPLICATION_SEPERATOR);
                        else application_IDs = new String[] { tmpApplicationValues };
                    } else {
                        application_IDs = null;
                    }
                } else {
                    // Properties keys not found error handeling here
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
            client_configurations_found = false;
        }
    }
    
    public static void generateClientPropertiesFile(String server_url) {
        
        Properties prop = new Properties();
        OutputStream output = null;
        
        try {
            output = new FileOutputStream(CLIENT_CONFIGURATIONS_FILE_PATH);
            
            if (server_url != null) prop.setProperty(KEY_SERVER_URL, server_url);
            else prop.setProperty(KEY_SERVER_URL, "");
            
            prop.setProperty(KEY_APPLICATION_IDS, "");
            
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
    
    public static void generateApplicationPropertiesFile() {
        
        Properties prop = new Properties();
        OutputStream output = null;
        
        try {
            output = new FileOutputStream(APPLICATION_CONFIGURATIONS_FILE_PATH);
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
    
    public static void generateClientPropertiesFile() {
        generateClientPropertiesFile(null);
    }
    
    public static void changeServerURL(String server_url) {
        File properties_file = new File(CLIENT_CONFIGURATIONS_FILE_PATH);
        
        if (properties_file.exists() && !properties_file.isDirectory()) {
            Properties properties = new Properties();
            FileInputStream properties_input_stream = null;
            FileOutputStream properties_output_stream = null;
            
            client_configurations_found = true;
            
            try {
                properties_input_stream = new FileInputStream(properties_file);
                properties.load(properties_input_stream);
                properties_input_stream.close();
                
                if (server_url != null) properties.setProperty(KEY_SERVER_URL, server_url);
                else properties.setProperty(KEY_SERVER_URL, "");
                
                properties_output_stream = new FileOutputStream(properties_file);
                properties.store(properties_output_stream, null);
                
            } catch (IOException e) {
                System.out.println("Client change url properties file exception: " + e);
            } finally {
                if (properties_input_stream != null) {
                    try { properties_input_stream.close(); }
                    catch (IOException e) { System.out.println("Properties change url properties file input stream close exception: " + e); }
                }
                
                if (properties_output_stream != null) {
                    try { properties_output_stream.close(); }
                    catch (IOException e) { System.out.println("Properties change url properties file output stream close exception: " + e); }
                }
            }
        } else {
            // Properties file not found error handeling here
            client_configurations_found = false;
            generateClientPropertiesFile(server_url);
        }
    }
    
    public static void clearServerURL() {
        changeServerURL(null);
    }
    
    private static void updateApplicationIDs(String application_id) {
        readPropertiesFile();
        
        File properties_file = new File(CLIENT_CONFIGURATIONS_FILE_PATH);
        
        if (properties_file.exists() && !properties_file.isDirectory()) {
            Properties properties = new Properties();
            FileInputStream properties_input_stream = null;
            FileOutputStream properties_output_stream = null;
            
            client_configurations_found = true;
            
            try {
                properties_input_stream = new FileInputStream(properties_file);
                properties.load(properties_input_stream);
                properties_input_stream.close();
                
                if (application_id != null) {
                    if (application_IDs == null ||
                        application_IDs.length == 0 || (
                            application_IDs.length == 1 && (
                                application_IDs[0] == null ||
                                application_IDs[0].length() == 0
                            )
                        )
                    ) {
                        properties.setProperty(KEY_APPLICATION_IDS, application_id);
                        application_IDs = new String[] { application_id };
                    } else {
                        String application_id_string = "";
                        for (String existing_application_id : application_IDs) {
                            application_id_string += existing_application_id + APPLICATION_SEPERATOR;
                        }
                        application_id_string += application_id;
                        
                        properties.setProperty(KEY_APPLICATION_IDS, application_id_string);
                        application_IDs = application_id_string.split(APPLICATION_SEPERATOR);
                    }
                } else {
                    properties.setProperty(KEY_APPLICATION_IDS, "");
                    application_IDs = null;
                }
                
                properties_output_stream = new FileOutputStream(CLIENT_CONFIGURATIONS_FILE_PATH);
                properties.store(properties_output_stream, null);
                
            } catch (IOException e) {
                System.out.println("Client update application ids properties file exception: " + e);
            } finally {
                if (properties_input_stream != null) {
                    try { properties_input_stream.close(); }
                    catch (IOException e) { System.out.println("Properties update application ids properties file input stream close exception: " + e); }
                }
                
                if (properties_output_stream != null) {
                    try { properties_output_stream.close(); }
                    catch (IOException e) { System.out.println("Properties update application ids properties file output stream close exception: " + e); }
                }
            }
        } else {
            // Properties file not found error handeling here
            client_configurations_found = false;
        }
    }
    
    public static void clearApplicationIDs() {
        updateApplicationIDs(null);
    }
    
    public static Application newApplication(String new_application_id) {
        
        if (new_application_id != null) {
            updateApplicationIDs(new_application_id);
            
            return new Application(new_application_id);
        }
        return null;
    }
    
    public static Application[] getApplicationConfigurations() {
        readPropertiesFile();
        
        int id_amount;
        if (application_IDs != null && (id_amount = application_IDs.length) >= 1) {
            Application[] applications = new Application[id_amount];
            
            for (int i = 0; i < id_amount; i++) {
                applications[i] = new Application(application_IDs[i]);
            }
            
            return applications;
        }
        return null;
    }
    
    public static final class Application {

        private final String APPLICATION_KEY_PREFIX = "Application";
        private final String APPLICATION_KEY_ID_CIRCUMFIX = ".";
    
        private final String KEY_PREFIX;
    
        private final String KEY_APPLICATION_NAME;
        private final String KEY_LOG_URI;
        private final String KEY_API_KEY;
        private final String KEY_UPDATE_INTERVAL;
        private final String KEY_LINE_ONE;
        private final String KEY_LINE_TWO;
        private final String KEY_LINE_THREE;
        
        public final String ID;

        public String application_name = null;
        public String log_uri = null;
        public String api_key = null;
        public String update_interval = null;
        public String line_one = null;
        public String line_two = null;
        public String line_three = null;
        
        public Application(String application_id) {
            this.KEY_PREFIX = this.APPLICATION_KEY_PREFIX +
                              this.APPLICATION_KEY_ID_CIRCUMFIX +
                              application_id +
                              this.APPLICATION_KEY_ID_CIRCUMFIX;
            
            this.ID = application_id;
            this.KEY_APPLICATION_NAME = this.KEY_PREFIX + "application_name";
            this.KEY_LOG_URI = this.KEY_PREFIX + "log_uri";
            this.KEY_API_KEY = this.KEY_PREFIX + "api_key";
            this.KEY_UPDATE_INTERVAL = this.KEY_PREFIX + "update_interval";
            this.KEY_LINE_ONE = this.KEY_PREFIX + "line_one";
            this.KEY_LINE_TWO = this.KEY_PREFIX + "line_two";
            this.KEY_LINE_THREE = this.KEY_PREFIX + "line_three";
            
            this.readPropertiesFile();
        }

        public void updateApplicationConfigurations(String application_name, String log_uri, String api_key, String update_interval, String line_one, String line_two, String line_three) {
            File properties_file = new File(APPLICATION_CONFIGURATIONS_FILE_PATH);
            
            if (properties_file.exists() && !properties_file.isDirectory()) {
                Properties properties = new Properties();
                FileInputStream properties_input_stream = null;
                FileOutputStream properties_output_stream = null;

                try {
                    properties_input_stream = new FileInputStream(properties_file);
                    properties.load(properties_input_stream);
                    properties_input_stream.close();

                    if (application_name != null) {
                        properties.setProperty(this.KEY_APPLICATION_NAME, application_name);
                        this.application_name = application_name;
                    } else {
                        properties.setProperty(this.KEY_APPLICATION_NAME, "");
                        this.application_name = null;
                    }

                    if (log_uri != null) {
                        properties.setProperty(this.KEY_LOG_URI, log_uri);
                        this.log_uri = log_uri;
                    } else {
                        properties.setProperty(this.KEY_LOG_URI, "");
                        this.log_uri = null;
                    }

                    if (api_key != null) {
                        properties.setProperty(this.KEY_API_KEY, api_key);
                        this.api_key = api_key;
                    } else {
                        properties.setProperty(this.KEY_API_KEY, "");
                        this.api_key = null;
                    }

                    if (update_interval != null) {
                        properties.setProperty(this.KEY_UPDATE_INTERVAL, update_interval);
                        this.update_interval = update_interval;
                    } else {
                        properties.setProperty(this.update_interval, "");
                        this.update_interval = null;
                    }

                    if (line_one != null) {
                        properties.setProperty(this.KEY_LINE_ONE, line_one);
                        this.line_one = line_one;
                    } else {
                        properties.setProperty(this.line_one, "");
                        this.line_one = null;
                    }

                    if (line_two != null) {
                        properties.setProperty(this.KEY_LINE_TWO, line_two);
                        this.line_two = line_two;
                    } else {
                        properties.setProperty(this.line_two, "");
                        this.line_two = null;
                    }

                    if (line_three != null) {
                        properties.setProperty(this.KEY_LINE_THREE, line_three);
                        this.line_three = line_three;
                    } else {
                        properties.setProperty(this.line_three, "");
                        this.line_three = null;
                    }

                    properties_output_stream = new FileOutputStream(properties_file);
                    properties.store(properties_output_stream, null);

                } catch (IOException e) {
                    System.out.println("Update application configurations properties file exception: " + e);
                } finally {
                    if (properties_input_stream != null) {
                        try { properties_input_stream.close(); }
                        catch (IOException e) { System.out.println("Update application configurations properties file input stream close exception: " + e); }
                    }

                    if (properties_output_stream != null) {
                        try { properties_output_stream.close(); }
                        catch (IOException e) { System.out.println("Update application configurations properties file output stream close exception: " + e); }
                    }
                }
            } else {
                // Properties file not found error handeling here
                generateApplicationPropertiesFile();
            }
        } 

        public void readPropertiesFile() {
            File properties_file = new File(APPLICATION_CONFIGURATIONS_FILE_PATH);

            if (properties_file.exists() && !properties_file.isDirectory()) {
                Properties properties = new Properties();
                FileInputStream properties_input_stream = null;

                try {
                    properties_input_stream = new FileInputStream(properties_file);
                    properties.load(properties_input_stream);
                    
                    this.application_name = properties.getProperty(this.KEY_APPLICATION_NAME, null);
                    this.log_uri = properties.getProperty(this.KEY_LOG_URI, null);
                    this.api_key = properties.getProperty(this.KEY_API_KEY, null);
                    this.update_interval = properties.getProperty(this.KEY_UPDATE_INTERVAL, null);
                    this.line_one = properties.getProperty(this.KEY_LINE_ONE, null);
                    this.line_two = properties.getProperty(this.KEY_LINE_TWO, null);
                    this.line_three = properties.getProperty(this.KEY_LINE_THREE, null);
                    
                } catch (IOException e) {
                    System.out.println("Application read properties file exception: " + e);
                } finally {
                    if (properties_input_stream != null) {
                        try { properties_input_stream.close(); }
                        catch (IOException e) { System.out.println("Application read properties file input stream close exception: " + e); }
                    }
                }
            } else {
                // Properties file not found error handeling here
                generateApplicationPropertiesFile();
            }
        }
    }
}