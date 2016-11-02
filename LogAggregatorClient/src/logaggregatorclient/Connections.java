package logaggregatorclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connections {
    
    private final String url = "http://localhost:8080/LogAggregatorServer/ClientServlet";
    
    public String AUTHENTICATION_ERROR_MESSAGE = null;
    
    public String APPLICATION_NAME_ERROR_MESSAGE = null;
    public String UPDATE_INTERVAL_ERROR_MESSAGE = null;
    
    public boolean authenticate(String username, String password) {
        try {
            URL url_object = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url_object.openConnection();
            
            connection.setRequestMethod("GET");
            connection.setRequestProperty("client-action", "authenticate");
            connection.setRequestProperty("username", username);
            connection.setRequestProperty("password", password);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String authenticated_header = connection.getHeaderField("authenticated");
                if (authenticated_header != null) {
                    if (authenticated_header.equals("true")) {
                        
                        //User information approved.
                        return true;
                    } else if (authenticated_header.equals("false") &&
                        connection.getHeaderField("authentication-problem") != null) {
                        
                        //User information denied.
                        this.AUTHENTICATION_ERROR_MESSAGE = connection.getHeaderField("authentication-problem");
                    }
                } else {
                    //Authenticated header not received.
                }
                connection.disconnect();
            } else {
                //Connection not OK!
            }
        } catch (Exception e) {
            System.out.println("Authenticate exception caught: " + e);
        }
        return false;
    }
    
    public String new_application(String username, String password, String application_name, String update_interval) {
        try {
            URL url_object = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url_object.openConnection();
            
            connection.setRequestMethod("GET");
            connection.setRequestProperty("client-action", "new_application");
            connection.setRequestProperty("application-name", application_name);
            connection.setRequestProperty("update-interval", update_interval);
            connection.setRequestProperty("username", username);
            connection.setRequestProperty("password", password);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String application_created_header = connection.getHeaderField("application-added");
                if (application_created_header != null) {
                    if (application_created_header.equals("true") &&
                        connection.getHeaderField("api-key") != null) {
                        
                        //Application added.
                        String api_key = connection.getHeaderField("api-key");
                        connection.disconnect();
                        return api_key;
                    } else if (application_created_header.equals("false")) {
                        
                        //New application denied.
                        if (connection.getHeaderField("application-name-problem") != null)
                            this.APPLICATION_NAME_ERROR_MESSAGE = connection.getHeaderField("application-name-problem");
                        
                        if (connection.getHeaderField("update-interval-problem") != null)
                            this.UPDATE_INTERVAL_ERROR_MESSAGE = connection.getHeaderField("update-interval-problem");
                    }
                } else {
                    //Application-added header not received.
                }
                connection.disconnect();
            } else {
                //Connection not OK!
            }
        } catch (Exception e) {
            System.out.println("New application exception caught: " + e);
        }
        return null;
    }
    
    public void send_logs(String api_key, String packed_log_source) {
        try {
            String boundary = "gg" + System.currentTimeMillis() + "gg";
            String LINE_FEED = "\r\n";
            
            URL url_object = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url_object.openConnection();
            
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("api-key", api_key);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            OutputStream output_stream = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output_stream), true);
            
            //File part begin
            File upload_file = new File(packed_log_source);
            
            //File part headers begin
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + upload_file.getName() + "\"").append(LINE_FEED);
            writer.append("Content-Type: application/octet-stream").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            //File part headers end
            
            //File part body begin
            FileInputStream input_stream = new FileInputStream(upload_file);
            byte[] buffer = new byte[4096];
            int bytes_read = -1;
            while ((bytes_read = input_stream.read(buffer)) != -1) {
                output_stream.write(buffer, 0, bytes_read);
            }
            output_stream.flush();
            input_stream.close();
            writer.append(LINE_FEED);
            writer.flush();
            //File part body end
            //File part end
            
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();
            
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Server returned OK status.");
                connection.disconnect();
            } else {
                System.out.println("Server returned non-OK status.");
            }
        } catch (Exception e) {
            System.out.println("Send logs exception caught: " + e);
        }
    }
}