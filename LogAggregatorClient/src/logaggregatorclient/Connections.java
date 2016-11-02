package logaggregatorclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Connections {
    
    private final String USER_AGENT = "Mozilla/5.0";
    private final String url = "http://localhost:8080/LogAggregatorServer/ClientServlet";
    
    public String AUTHENTICATION_ERROR_MESSAGE = null;
    
    public String APPLICATION_NAME_ERROR_MESSAGE = null;
    public String UPDATE_INTERVAL_ERROR_MESSAGE = null;
    
    public boolean authenticate(String username, String password) {
        try {
            URL url_object = new URL(this.url);
            HttpURLConnection connection = (HttpURLConnection) url_object.openConnection();
            
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", this.USER_AGENT);
            connection.setRequestProperty("client-action", "authenticate");
            connection.setRequestProperty("username", username);
            connection.setRequestProperty("password", password);

            if (connection.getResponseCode() == 200) {
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
            connection.setRequestProperty("User-Agent", this.USER_AGENT);
            connection.setRequestProperty("client-action", "new_application");
            connection.setRequestProperty("application-name", application_name);
            connection.setRequestProperty("update-interval", update_interval);
            connection.setRequestProperty("username", username);
            connection.setRequestProperty("password", password);

            if (connection.getResponseCode() == 200) {
                String application_created_header = connection.getHeaderField("application-added");
                if (application_created_header != null) {
                    if (application_created_header.equals("true") &&
                        connection.getHeaderField("api-key") != null) {
                        
                        //Application added.
                        return connection.getHeaderField("api-key");
                    } else if (application_created_header.equals("false")) {
                        
                        //New application denied.
                        if (connection.getHeaderField("application-name-problem") != null)
                            this.APPLICATION_NAME_ERROR_MESSAGE = connection.getHeaderField("application-name-problem");
                        
                        if (connection.getHeaderField("update-interval-problem") != null)
                            this.UPDATE_INTERVAL_ERROR_MESSAGE = connection.getHeaderField("update-interval-problem");
                    }
                } else {
                    //Authenticated header not received.
                }
            } else {
                //Connection not OK!
            }
        } catch (Exception e) {
            System.out.println("Authenticate exception caught: " + e);
        }
        return null;
    }
    
    public void post() throws MalformedURLException, ProtocolException, IOException {
//        String url = "http://localhost:8080/LogAggregatorServer/rhexample";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
    }
}
