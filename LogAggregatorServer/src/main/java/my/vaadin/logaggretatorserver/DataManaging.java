package my.vaadin.logaggretatorserver;

import java.util.Base64;
import java.util.Random;

public class DataManaging {

    public static String generateAPIKey(String identifier) {
        byte[] key = new byte[32];
        new Random().nextBytes(key);
        return identifier + "gg" + Base64.getEncoder().encodeToString(key);
    }
    
    public static String hashString(String received_string) {
        
        return Integer.toString(received_string.hashCode());
    }
}