package logaggregatorclient;

public class DataManaging {
    
    public static String hashString(String received_string) {
        
        return Integer.toString(received_string.hashCode());
    }
    
    public static void newApplication(String username,
                                      String password,
                                      String application_name,
                                      String application_source_uri,
                                      String update_interval,
                                      String interval_option) {

        Connections connections = new Connections();
        
        String update_interval_calculated = update_interval;
        if (interval_option.equals(GUI.COMBOBOX_HOURS)) {
            update_interval_calculated = Integer.toString(Integer.parseInt(update_interval) * 60 * 60);
        } else if (interval_option.equals(GUI.COMBOBOX_MINUTES)) {
            update_interval_calculated = Integer.toString(Integer.parseInt(update_interval) * 60);
        }
        
        Connections.ServerResponses responses = connections.new_application(
                username,
                password,
                application_name,
                update_interval_calculated);
        
        LogHandler log_handler = new LogHandler(responses.application_id);
        log_handler.read(application_source_uri, null, null, null);
        
        Configurations.Application applications = Configurations.newApplication(responses.application_id);
        
        applications.updateApplicationConfigurations(
                application_name,
                application_source_uri,
                responses.application_api_key,
                update_interval_calculated,
                log_handler.last_read_line_one,
                log_handler.last_read_line_two,
                log_handler.last_read_line_three
        );
        
        connections.send_logs(responses.application_api_key,
                              log_handler.COMPLETE_ZIP_PATH);
    }
}