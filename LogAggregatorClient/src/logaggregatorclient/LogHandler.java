package logaggregatorclient;

public class LogHandler {
    /*
    This class will focus on reading, sorting and then passing on 
    the log files given.
    */
    
    public void add_new_log(String source_URI, String service, int update_frequency) {
        /*
        This method will only be called to when there's a new service being
        being added to the database. It will not deal with the the .log file,
        it will instead prepare the necessary additions to the database to
        make way for the .log file to be sent.

        The source_URI will point to the .log file stored on the clients device.
        
        The service will be the service id pointing towards which service this
        .log information will be stored to.
        
        The update_frequency will be the amount of seconds inbetween everytime
        the .log will be sent to the database server.
        */
    }
    
    public void read(String source_URI, String service, String last_line) {
        /*
        This method will be used by both the automatic update script aswell as 
        the add_new_log method. It will focus on reading, parsing and passing
        the given log.
        
        The source_URI will point to the .log file stored on the clients device.
        
        The service will be the service id pointing towards which service this
        .log information will be stored to.
        */
    }

    public void read(String source_URI, String service) {
        read(source_URI, service, null);
        /*
        This is just a method overload, used to set a default value
        if there is no last_line provided to the main read  method.

        Thus this method should be left alone.
        */
    }
}
