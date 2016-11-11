package my.vaadin.logaggretatorserver;

import java.util.HashMap;

public class ApplicationRow {
    
    public String id = null;
    public CompanyRow company = null;
    public String name = null;
    public String latest_update = null;
    public String update_interval = null;
    public String api_key = null;
    public String log_type = null;
    public LogRow[] logs = null;
    
    private final ServerDataBase database_connection = new ServerDataBase();
    
    public ApplicationRow(String id, String company_id, String name, String latest_update, String update_interval, String api_key, String log_type, boolean load_logs) {
        
        this.id = id;
        this.company = new CompanyRow(company_id);
        this.name = name;
        this.latest_update = latest_update;
        this.update_interval = update_interval;
        this.api_key = api_key;
        this.log_type = log_type;
        
        if (load_logs) this.logs = this.get_logs(id);
    }
    
    public ApplicationRow(String id, String company_id, String name, String latest_update, String update_interval, String api_key, String log_type) {
        this(id, company_id, name, latest_update, update_interval, api_key, log_type, true);
    }
    
    public ApplicationRow(String id, boolean load_logs) {
        
        String[] columnQuery = {
                "company_id",
                "name",
                "latest_update",
                "update_interval",
                "api_key",
                "log_type"
        };

        HashMap whereQuery = new HashMap();
        whereQuery.put("id", id);

        this.database_connection.connect();
        String[][] select = this.database_connection.select(columnQuery, "application", whereQuery);
        this.database_connection.close();
        
        if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
            this.id = id;
            this.company = new CompanyRow(select[0][0]);
            this.name = select[0][1];
            this.latest_update = select[0][2];
            this.update_interval = select[0][3];
            this.api_key = select[0][4];
            this.log_type = select[0][5];
            
            if (load_logs) this.logs = this.get_logs(id);
        }
    }
    
    public ApplicationRow(String id) {
        this(id, true);
    }
    
    private LogRow[] get_logs(String application_id) {

        String[] columnQuery = {
                "id",
                "date",
                "event",
                "special_event"
        };

        HashMap whereQuery = new HashMap();
        whereQuery.put("application_id", application_id);

        this.database_connection.connect();
        String[][] select = this.database_connection.select(columnQuery, "log", whereQuery);
        this.database_connection.close();

        if (select != null && select.length >= 1 && select[0].length == columnQuery.length) {
            LogRow[] results = new LogRow[select.length];
            
            for (int i = 0; i < select.length; i++) {
                results[i] = new LogRow(select[i][0], application_id, select[i][1], select[i][2], select[i][3]);
            }
            
            return results;
        }

        return null;
    }

    public LogRow[] get_logs() {
        return get_logs(this.id);
    }
}