package my.vaadin.logaggretatorserver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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