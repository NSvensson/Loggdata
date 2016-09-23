package clientlogcollector;

public class AutomaticUpdate {
    /*
    This class will focus on updating the logs on the database server 
    automatically every given intervall.
    */
    
    private final String source_URI;
    private final int update_frequency;
    private final String service;
    
    public AutomaticUpdate(String source_URI, int update_frequency, String service) {
        this.source_URI = source_URI;
        this.update_frequency = update_frequency;
        this.service = service;
        
        /*
        The source_URI will point to the .log file stored on the clients device.

        The service will be the service id pointing towards which service this
        .log information will be stored to.

        The update_frequency will be the amount of seconds inbetween everytime
        the .log will be sent to the database server.
        */
    }
}
