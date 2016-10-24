package my.vaadin.logaggretatorserver;

public class LogRow {
    
    public String id = null;
    public String application_id = null;
    public String date = null;
    public String event = null;
    public String special_event = null;
    
    public LogRow(String id, String application_id, String date, String event, String special_event) {

        this.id = id;
        this.application_id = application_id;
        this.date = date;
        this.event = event;
        this.special_event = special_event;
    }
    
    public LogRow(String id, String application_id, String date, String event) {
        this(id, application_id, date, event, null);
    }
}