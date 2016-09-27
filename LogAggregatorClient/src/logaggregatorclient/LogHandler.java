package logaggregatorclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogHandler {
    /*
    This class will focus on reading, sorting and then passing on 
    the log files given.
    */
    
    public static void packLog(String[][] stringArray) {
        try {
            String logg = "";

            for (String[] row : stringArray) {
                for (String value : row) {
                    logg += value + " ";
                }
                logg += "\n";
            }

            File file = new File("./filename.txt");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(logg);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] buffer = new byte[1024];

        try {

            FileOutputStream fos = new FileOutputStream("./MyFile.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry("filename.txt");
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream("./filename.txt");

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

            //remember close it
            zos.close();

            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
