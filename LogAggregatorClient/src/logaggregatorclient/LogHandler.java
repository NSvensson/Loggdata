package logaggregatorclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogHandler {
    /*
    This class will focus on reading, sorting and then passing on 
    the log files given.
    */
    
    
    public static void read(String source_URI, String service, String last_line) {
        /*
        This method will be used by both the automatic update script aswell as 
        the add_new_log method. It will focus on reading, parsing and passing
        the given log.
        
        The source_URI will point to the .log file stored on the clients device.
        
        The service will be the service id pointing towards which service this
        .log information will be stored to.
        */
        //source_URI = "src/source/console.log"
        List<List<String>> combined2d = new ArrayList<>();        
         try{
            try (FileInputStream fstream = new FileInputStream(source_URI)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                /* read log line by line */
                while ((strLine = br.readLine()) != null)   {
                    /* parse strLine to obtain what you want */
                    String regex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:{0,1}\\d{0,2}";
                    
                    Matcher m = Pattern.compile(regex).matcher(strLine);
                    
                    if (m.find()) {
                        
                        String res = m.group(0);
                        //String arr1[] = res.split("");
                        //String ses = m1.group(0);
                        //System.out.println(ses);
                        
                        String utan_datum  = strLine.replaceAll(res, "");
                        //String arr[] = utan_datum.split("");
                        //System.out.println (m.group(0) + "     " + utan_datum);
                        //String arr2[] = utan_datum.split("");
                        List<String> myList = new ArrayList<>();
                        myList.add(res);
                        myList.add(utan_datum);
//                        List<String> myList = new ArrayList<>(Arrays.asList());
//                        List<String> myList1 = new ArrayList<>(Arrays.asList(utan_datum.split(",")));
                        //System.out.println(myList + " " + myList1);

                        combined2d.add(myList);
//                        combined2d.add(myList1);

                        //return combined2d;
                    }
                    
                    else  {
                        System.out.println("");
                    }
                    
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        System.out.println(combined2d);

        String[][] resultsArray = new String[combined2d.size()][];
        for (int i = 0; i < combined2d.size(); i++) {
            List<String> row = combined2d.get(i);
            resultsArray[i] = row.toArray(new String[row.size()]);
        }
    }   
    
    
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
    
    

    public void read(String source_URI, String service) {
        read(source_URI, service, null);
        /*
        This is just a method overload, used to set a default value
        if there is no last_line provided to the main read  method.

        Thus this method should be left alone.
        */
    }
}
