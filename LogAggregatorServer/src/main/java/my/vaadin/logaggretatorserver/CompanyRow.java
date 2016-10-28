package my.vaadin.logaggretatorserver;

import java.util.HashMap;

public class CompanyRow {
    
    public String id = null;
    public String name = null;
    public String website = null;
    public String details = null;
    
    private final ServerDataBase database_connection = new ServerDataBase();

    public CompanyRow(String id) {
        
        String[] columnQuery = {
                "name",
                "website",
                "details"
        };

        HashMap whereQuery = new HashMap();
        whereQuery.put("id", id);

        this.database_connection.connect();
        String[][] select = this.database_connection.select(columnQuery, "company", whereQuery);
        this.database_connection.close();
        
        if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
            this.id = id;
            this.name = select[0][0];
            this.website = select[0][1];
            this.details = select[0][2];
        }
    }
    
    public CompanyRow(String id, String name, String website, String details) {
        
        this.id = id;
        this.name = name;
        if (website != null) this.website = website;
        if (details != null) this.details = details;
    }
}
