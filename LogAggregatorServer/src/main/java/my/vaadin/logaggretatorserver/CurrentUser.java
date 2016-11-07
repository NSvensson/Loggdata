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
    
    public CurrentUser(String username, String password, boolean load_applications) {
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
            if (load_applications) load_available_applications();
        }
    }

    public CurrentUser(String username, String password) {
        this(username, password, true);
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
    
    public CurrentUser(String id, String company_id, String first_name, String last_name, String email, String username, String user_group_id) {
        this.id = id;
        this.company = new CompanyRow(company_id);
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.username = username;
        this.user_group = new UserGroups(user_group_id);
    }
    
    public void load_available_applications() {
        String[] columnQuery = {
                "id",
                "name",
                "latest_update",
                "update_interval",
                "api_key",
                "log_type"
        };

        HashMap whereQuery = new HashMap();
        whereQuery.put("company_id", this.company.id);

        this.database_connection.connect();
        String[][] select = this.database_connection.select(columnQuery, "application", whereQuery);
        this.database_connection.close();

        if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
            ApplicationRow[] results = new ApplicationRow[select.length];
            
            for (int i = 0; i < select.length; i++) {
                results[i] = new ApplicationRow(select[i][0], this.company.id, select[i][1], select[i][2], select[i][3], select[i][4], select[i][5]);
            }
            
            this.applications = results;
        }
    }
    
//    private ApplicationRow[] available_applications() {
//        return available_applications(this.company.id);
//    }
}