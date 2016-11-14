package logaggregatorclient;

import java.util.Timer;
import java.util.TimerTask;

public final class AutomaticUpdate {
    
    private ApplicationUpdater[] application_updaters;
    
    public AutomaticUpdate() {
        
        this.loadApplications();
    }
    
    public void loadApplications() {
        if (Configurations.application_IDs.length >= 1) {
            this.application_updaters = new ApplicationUpdater[Configurations.application_IDs.length];
            
            Configurations.Application[] applications = Configurations.getApplicationConfigurations();
            
            for (int i = 0; i < Configurations.application_IDs.length; i++) {
                this.application_updaters[i] = new ApplicationUpdater(applications[i]);
            }
        }
    }
    
    public void startAllUpdaters() {
        if (this.application_updaters != null && this.application_updaters.length >= 1) {
            for (ApplicationUpdater application_updater : this.application_updaters) {
                application_updater.start();
            }
        } else {
            this.loadApplications();
        }
    }
    
    public void stopAllUpdaters() {
        if (this.application_updaters != null && this.application_updaters.length >= 1) {
            for (ApplicationUpdater application_updater : this.application_updaters) {
                application_updater.stop();
            }
        } else {
            this.loadApplications();
        }
    }
    
    private class ApplicationUpdater {

        private final Configurations.Application application;
        private final Timer timer = new Timer();

        public ApplicationUpdater(Configurations.Application application) {
            this.application = application;
        }

        public void start() {
            if (application.update_interval != null) {
                timer.schedule(
                        new UpdateTask(),
                        0,
                        (Integer.parseInt(application.update_interval) * 1000));
            }
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
                log_handler = new LogHandler(application.ID);

                log_handler.read(
                        application.log_uri,
                        application.line_one,
                        application.line_two,
                        application.line_three
                );

                Connections.ServerResponses responses = connections.send_logs(
                        application.api_key,
                        log_handler.COMPLETE_ZIP_PATH
                );

                if (log_handler.last_read_line_one != null &&
                    log_handler.last_read_line_two != null &&
                    log_handler.last_read_line_three != null) {

                    application.updateApplicationConfigurations(
                            responses.application_name,
                            application.log_uri,
                            application.api_key,
                            responses.application_update_interval,
                            log_handler.last_read_line_one,
                            log_handler.last_read_line_two,
                            log_handler.last_read_line_three
                    );
                }
            }
        }
    }
}