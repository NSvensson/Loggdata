package my.vaadin.logaggretatorserver;

import java.util.HashMap;

public class UserGroups {
    
    public String id = null;
    public String name = null;
    public boolean view_logs = false;
    public boolean manage_applications = false;
    public boolean manage_users = false;
    public boolean manage_companies = false;
    public boolean manage_groups = false;

    private final ServerDataBase database_connection = new ServerDataBase();
    
    public UserGroups(String id) {
        if (!id.equals("0")) {
            String[] columnQuery = {
                    "name",
                    "view_logs",
                    "manage_applications",
                    "manage_users",
                    "manage_companies",
                    "manage_groups"
            };

            HashMap whereQuery = new HashMap();
            whereQuery.put("id", id);

            this.database_connection.connect();
            String[][] select = this.database_connection.select(columnQuery, "user_groups", whereQuery);
            this.database_connection.close();

            if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
                this.id = id;
                this.name = select[0][0];
                this.view_logs = select[0][1].equals("1");
                this.manage_applications = select[0][2].equals("1");
                this.manage_users = select[0][3].equals("1");
                this.manage_companies = select[0][4].equals("1");
                this.manage_groups = select[0][5].equals("1");
            }
        }
    }

    public UserGroups() {
        this("0");
    }
}