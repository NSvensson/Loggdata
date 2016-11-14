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
    
    public final String COMPLETE_ZIP_PATH;
    private final String COMPLETE_LOG_PATH;
    
    private final String ZIP_PATH = "./";
    private final String ZIP_NAME = "PackedLog";
    private final String ZIP_FILE_TYPE = ".zip";
    
    private final String LOG_PATH = "./";
    private final String LOG_NAME = "Log";
    private final String LOG_FILE_TYPE = ".txt";
    
    public String last_read_line_one = null;
    public String last_read_line_two = null;
    public String last_read_line_three = null;
    
    public LogHandler(String application_id) {
        
        this.COMPLETE_ZIP_PATH = this.ZIP_PATH + this.ZIP_NAME + application_id + this.ZIP_FILE_TYPE;
        this.COMPLETE_LOG_PATH = this.LOG_PATH + this.LOG_NAME + this.LOG_FILE_TYPE;
    }
    
    public void read(String source_URI, String line_one, String line_two, String line_three) {
        /*
        This method will be used by both the automatic update script aswell as 
        the add_new_log method. It will focus on reading, parsing and passing
        the given log.
        
        The source_URI will point to the .log file stored on the clients device.
        
        The service will be the service id pointing towards which service this
        .log information will be stored to.
        */
        List<List<String>> combined2d = new ArrayList<>();        
        String regex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:{0,1}\\d{0,2}";
        Pattern pat = Pattern.compile(regex);
        
        try {
            try (FileInputStream fstream = new FileInputStream(source_URI)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                /* read log line by line */
                while ((strLine = br.readLine()) != null)   {
                    /* parse strLine to obtain what you want */
                    
                    Matcher m = pat.matcher(strLine);
                    
                    List<String> myList = new ArrayList<>();
                    if (m.find()) {
                        
                        String res = m.group(0);
                        String utan_datum  = strLine.replaceAll(res, "");
                        
                        myList.add(res);
                        myList.add(utan_datum);

                        combined2d.add(myList);
                    } else  {
                        if (!combined2d.isEmpty() && strLine != null && strLine.length() >= 1) {
                            myList.add(combined2d.get(combined2d.size() - 1).get(0));
                            myList.add(combined2d.get(combined2d.size() - 1).get(1) + "\n" + strLine);
                            
                            combined2d.set(combined2d.size() - 1, myList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        int results_length = combined2d.size();
        String[][] resultsArray = new String[results_length][];
        for (int i = 0; i < results_length; i++) {
            List<String> row = combined2d.get((results_length - 1) - i);
            String[] row_array = row.toArray(new String[row.size()]);
            
            if (results_length >= 3) {
                String line_one_check = row_array[0] + row_array[1];
                if (line_one_check.equals(line_one)) {
                    String[] row_two = combined2d.get((results_length - 2) - i).toArray(new String[row.size()]);
                    String line_two_check = row_two[0] + row_two[1];
                    
                    String[] row_three = combined2d.get((results_length - 3) - i).toArray(new String[row.size()]);
                    String line_three_check = row_three[0] + row_three[1];
                    
                    if (line_two_check.equals(line_two) && line_three_check.equals(line_three)) {
                        break;
                    } else {
                        resultsArray[i] = row_array;
                    }
                } else {
                    resultsArray[i] = row_array;
                }
            } else {
                resultsArray[i] = row_array;
            }
        }
        
        if (resultsArray.length >= 3 &&
            resultsArray[0] != null) {
            
            if (resultsArray[1] == null && resultsArray[2] == null) {
                this.last_read_line_three = line_two;
                this.last_read_line_two = line_one;
                this.last_read_line_one = resultsArray[0][0] + resultsArray[0][1];
            } else if (resultsArray[1] != null && resultsArray[2] == null) {
                this.last_read_line_three = line_one;
                this.last_read_line_two = resultsArray[1][0] + resultsArray[1][1];
                this.last_read_line_one = resultsArray[0][0] + resultsArray[0][1];
            } else if (resultsArray[1] != null && resultsArray[2] != null) {
                this.last_read_line_one = resultsArray[0][0] + resultsArray[0][1];
                this.last_read_line_two = resultsArray[1][0] + resultsArray[1][1];
                this.last_read_line_three = resultsArray[2][0] + resultsArray[2][1];
            }
        }
        packLog(resultsArray);
    }
    
    public void packLog(String[][] stringArray) {
        try {
            String log = "";

            for (String[] row : stringArray) {
                if (row == null) break;
                for (String value : row) {
                    log += value;
                }
                log += "\n";
            }

            File file = new File(this.COMPLETE_LOG_PATH);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(log);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] buffer = new byte[4096];

        try {

            FileOutputStream fos = new FileOutputStream(this.COMPLETE_ZIP_PATH);
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze = new ZipEntry(this.LOG_NAME + this.LOG_FILE_TYPE);
            zos.putNextEntry(ze);
            FileInputStream in = new FileInputStream(this.COMPLETE_LOG_PATH);

            int len;
            while ((len = in.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }

            in.close();
            zos.closeEntry();

            //remember close it
            zos.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
