package my.vaadin.logaggretatorserver;

import java.util.HashMap;

public class CurrentUser {
    
    public boolean is_authenticated = false;
    public String id = null;
    public CompanyRow company = null;
    public String first_name = null;
    public String last_name = null;
    public String email = null;
    public String username = null;
    public ApplicationRow[] applications = null;
    public UserGroups user_group = null;
    
    private final ServerDataBase database_connection = new ServerDataBase();
    
    public CurrentUser(String username, String password) {
        String[] columnQuery = {
                    "id",
                    "company_id",
                    "first_name",
                    "last_name",
                    "email",
                    "username",
                    "user_group_id"
        };

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
            this.company = new CompanyRow(select[0][1]);
            this.first_name = select[0][2];
            this.last_name = select[0][3];
            this.email = select[0][4];
            this.username = select[0][5];
            this.user_group = new UserGroups(select[0][6]);
            this.applications = available_applications(select[0][1]);
        }
    }
    
    public CurrentUser(String id) {
        String[] columnQuery = {
                    "company_id",
                    "first_name",
                    "last_name",
                    "email",
                    "username",
                    "user_group_id"
        };

        HashMap whereQuery = new HashMap();
        whereQuery.put("id", id);
        
        this.database_connection.connect();
        String[][] select = this.database_connection.select(columnQuery, "user", whereQuery);
        this.database_connection.close();

        if (select != null && select.length == 1 && select[0].length == columnQuery.length) {
            this.id = id;
            this.company = new CompanyRow(select[0][0]);
            this.first_name = select[0][1];
            this.last_name = select[0][2];
            this.email = select[0][3];
            this.username = select[0][4];
            this.user_group = new UserGroups(select[0][5]);
        }
    }
    
    private ApplicationRow[] available_applications(String company_id) {
        String[] columnQuery = {
                "id",
                "name",
                "latest_update",
                "update_interval",
                "api_key",
                "log_type"
        };

        HashMap whereQuery = new HashMap();
        whereQuery.put("company_id", company_id);

        this.database_connection.connect();
        String[][] select = this.database_connection.select(columnQuery, "application", whereQuery);
        this.database_connection.close();

        if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
            ApplicationRow[] results = new ApplicationRow[select.length];
            
            for (int i = 0; i < select.length; i++) {
                results[i] = new ApplicationRow(select[i][0], company_id, select[i][1], select[i][2], select[i][3], select[i][4], select[i][5]);
            }
            
            return results;
        }

        return null;
    }
    
//    private ApplicationRow[] available_applications() {
//        return available_applications(this.company.id);
//    }
}