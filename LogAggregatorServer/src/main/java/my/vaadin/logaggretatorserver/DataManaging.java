package my.vaadin.logaggretatorserver;

import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

public class DataManaging {

    public static String generateAPIKey(String identifier) {
        byte[] key = new byte[32];
        new Random().nextBytes(key);
        return identifier + "gg" + Base64.getEncoder().encodeToString(key);
    }
    
    public static String hashString(String received_string) {
        
        return Integer.toString(received_string.hashCode());
    }
    
    //Authenticate API key method start.
    /**
     * Authenticates the provided API key.
     * 
     * @param api_key API key being authenticated.
     * @return ApplicationRow containing the information of the application related to the authenticated API key.
     */
    public static ApplicationRow authenticate_API_key(String api_key) {
        if (api_key != null) {
            
            ServerDataBase database_connection = new ServerDataBase();

            String[] columnQuery = {
                    "id",
                    "company_id",
                    "name",
                    "latest_update",
                    "update_interval",
                    "log_type"
            };

            HashMap whereQuery = new HashMap();
            whereQuery.put("api_key", api_key);

            database_connection.connect();
            String[][] select = database_connection.select(columnQuery, "application", whereQuery);
            database_connection.close();

            if (select != null && select.length == 1 && select[0].length == columnQuery.length) {
                return new ApplicationRow(
                        select[0][0],
                        select[0][1],
                        select[0][2],
                        select[0][3],
                        select[0][4],
                        api_key,
                        select[0][5],
                        false
                );
            }
        }
        return null;
    }
    //Authenticate API key method end.
    
}