package my.vaadin.logaggretatorserver;

import java.util.HashMap;

public class CurrentUser {
    
    public boolean is_authenticated = false;
    public String id = null;
    public String company_id = null;
    public String first_name = null;
    public String last_name = null;
    public String email = null;
    public String username = null;
    
    private final ServerDataBase database_connection = new ServerDataBase();
    
    public CurrentUser(String username, String password) {
        HashMap whereQuery = new HashMap();
        whereQuery.put("username", username);
        whereQuery.put("password", password);
        
        String[] columnQuery = {
                    "id",
                    "company_id",
                    "first_name",
                    "last_name",
                    "email",
                    "username"};

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
        }
    }
}