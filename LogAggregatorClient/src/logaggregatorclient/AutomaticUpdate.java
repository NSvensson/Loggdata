package logaggregatorclient;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AutomaticUpdate {
    
    private final DataManaging data_managing = new DataManaging();
    private HashMap<String, String> configuration = null;
    private final Timer timer = new Timer();
    
    public AutomaticUpdate() {
        configuration = data_managing.readPropertiesFile();
    }
    
    public void start() {
        timer.schedule(new UpdateTask(), 0, (Integer.parseInt(configuration.get(data_managing.UPDATE_INTERVAL)) * 1000));
    }
    
    public void stop() {
        timer.cancel();
    }
    
    private class UpdateTask extends TimerTask {
        /*
        This class will focus on updating the logs on the database server 
        automatically every given interval.
        */
        
        private final Connections connections = new Connections();
        private LogHandler log_handler = null;
        
        @Override
        public void run() {
            log_handler = new LogHandler();
            configuration = data_managing.readPropertiesFile();
            
            log_handler.read(
                    configuration.get(data_managing.LOG_URI),
                    configuration.get(data_managing.LINE_ONE),
                    configuration.get(data_managing.LINE_TWO),
                    configuration.get(data_managing.LINE_THREE)
            );
            
            connections.send_logs(
                    configuration.get(data_managing.API_KEY),
                    log_handler.zip_path
            );
            
            if (log_handler.last_read_line_one != null &&
                log_handler.last_read_line_two != null &&
                log_handler.last_read_line_three != null) {
                
                data_managing.generatePropertiesFile(
                        configuration.get(data_managing.LOG_URI),
                        configuration.get(data_managing.API_KEY),
                        configuration.get(data_managing.UPDATE_INTERVAL),
                        log_handler.last_read_line_one,
                        log_handler.last_read_line_two,
                        log_handler.last_read_line_three
                );
            }
        }
    }
}