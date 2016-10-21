package my.vaadin.logaggretatorserver;

import java.util.ArrayList;
import java.util.HashMap;

public class CurrentUser {
    
    public boolean is_authenticated = false;
    public String id = null;
    public String company_id = null;
    public String first_name = null;
    public String last_name = null;
    public String email = null;
    public String username = null;
    public HashMap<String, String> available_applications = null;
    
    private final ServerDataBase database_connection = new ServerDataBase();
    
    public CurrentUser(String username, String password) {
        String[] columnQuery = {
                    "id",
                    "company_id",
                    "first_name",
                    "last_name",
                    "email",
                    "username"};

        HashMap whereQuery = new HashMap();
        whereQuery.put("username", username);
        whereQuery.put("password", password);
        
        this.database_connection.connect();
        String[][] select = this.database_connection.select(columnQuery, "user", whereQuery);
        this.database_connection.close();

        if (select != null && select.length == 1 && select[0].length == columnQuery.length) {
            // Authenticated.
            this.is_authenticated = true;
            this.id = select[0][0];
            this.company_id = select[0][1];
            this.first_name = select[0][2];
            this.last_name = select[0][3];
            this.email = select[0][4];
            this.username = select[0][5];
            
            this.available_applications = available_applications();
        }
    }
    
    private HashMap<String, String> available_applications() {
        if (this.is_authenticated) {
            HashMap<String, String> results = new HashMap<String, String>();
            String[] columnQuery = {"id", "name"};

            HashMap whereQuery = new HashMap();
            whereQuery.put("company_id", this.company_id);

            this.database_connection.connect();
            String[][] select = this.database_connection.select(columnQuery, "application", whereQuery);
            this.database_connection.close();

            if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
                for (String[] row : select) {
                    results.put(row[0], row[1]);
                }
            }
            
            return results;
        }
        return null;
    }
    
    public String[][] available_logs() {
        if (this.is_authenticated) {
            String[][] results;
            String[] columnQuery = {"application_id", "date", "event"};

            HashMap whereQuery = new HashMap();
            whereQuery.put("application_id", new ArrayList(this.available_applications.keySet()));

            this.database_connection.connect();
            results = this.database_connection.select_advanced(columnQuery, "log", whereQuery);
            this.database_connection.close();

            if (results != null && results.length >= 1 && results[0].length == columnQuery.length) {
                for (int i = 0; i < results.length; i++) {
                    results[i][0] = this.available_applications.get(results[i][0]);
                }
            }
            
            return results;
        }
        return null;
    }
}