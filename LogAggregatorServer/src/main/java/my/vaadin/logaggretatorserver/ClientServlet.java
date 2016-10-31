package my.vaadin.logaggretatorserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
public class ClientServlet extends HttpServlet {
    private final Administration administration = new Administration(new UserGroups("1"));

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String servlet_URI = "http://localhost:8080/LogAggregatorServer/LogServlet";
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<body style=\"display: none;\">");
            out.println("<form action=\"" + servlet_URI + "\" method=\"post\" enctype=\"multipart/form-data\">");
            out.println("<input type=\"file\" name=\"file\">");
            out.println("<input type=\"submit\" name=\"Submit\" value=\"Upload\">");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        
        System.out.println("Post recieved.");

        /*
        First check if the header containing a api-key is provided 
        (this header is required for identification of the provided zip file)
        */
        if (request.getHeader("api-key") != null) {
            /*
            Check if the provided API key is valid and obtain the id related to 
            the key, then pick out the file from the multipart request.
            */
            String application_id = this.administration.application.authenticate_API_key(request.getHeader("api-key"));
            
            Part given_file;
            if (application_id != null && (given_file = request.getPart("file")) != null) {
                System.out.println("API key is valid.");

                /*
                Check if the content-type of the provided file is zip or
                anything related. (This however is a little bit unnecessary
                due to the byte check done further down.)
                */
                String contentType = given_file.getContentType();
                if (contentType != null && (
                        contentType.contains("application/zip") || 
                        contentType.contains("application/octet-stream") || 
                        contentType.contains("application/x-zip-compressed")
                        )) {

                    /*
                    Create a inputstream to read the provided file byte after byte.
                    */
                    InputStream input_stream = given_file.getInputStream();
                    ByteArrayOutputStream byte_array_output_stream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[input_stream.available()];

                    int line = -1;
                    while ((line = input_stream.read(buffer)) != -1) {
                        byte_array_output_stream.write(buffer, 0, line);
                    }

                    byte[] read_file = byte_array_output_stream.toByteArray();

                    /*
                    Here we check if the provided file really is a zip file.
                    
                    The first 4 bytes can be used to determine whether the read file
                    is a zip or not. The 4 magic numbers to determine if it is a zip
                    file is: 80 75 3 4.
                    */
                    if (read_file.length >= 4 && 
                            read_file[0] == 80 && 
                            read_file[1] == 75 && 
                            read_file[2] == 3 && 
                            read_file[3] == 4) {
                        
                        /*
                        Obtain the first (and expected to be the only) entry.
                        */
                        ZipInputStream zip_input_stream = new ZipInputStream(given_file.getInputStream());
                        ZipEntry entry = zip_input_stream.getNextEntry();
                        
                        if (entry.getName().equals("Log.txt")) {
                            System.out.println("Log.txt found.");
                            
                            /*
                            Here we pick out the date and the event provided in the log file
                            and put it in a array.
                            */
                            String regex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:{0,1}\\d{0,2}";
                            Pattern pattern = Pattern.compile(regex);
                            
                            ArrayList<String[]> results_array_list = new ArrayList<String[]>();
                            
                            BufferedReader br = new BufferedReader(new InputStreamReader(zip_input_stream));
                            
                            String read_line;
                            while ((read_line = br.readLine()) != null) {
                                
                                Matcher matcher = pattern.matcher(read_line);
                                
                                if (matcher.find()) {
                                    String tmp_date = matcher.group(0);
                                    String tmp_event = read_line.replaceAll(tmp_date, "");
                                    
                                    results_array_list.add(new String[] { tmp_date, tmp_event });
                                }
                            }
                            
                            if (!results_array_list.isEmpty()) {
                                String[][] results = new String[results_array_list.size()][2];
                                for (int i = 0; i < results_array_list.size(); i++) {
                                    results[i] = new String[] { results_array_list.get(i)[0], results_array_list.get(i)[1] };
                                }
                                
                                /*
                                Once the array containing the dates and events found in the provided log
                                is built, we send it to the administration object to process and insert
                                the logs found.
                                */
                                if (administration.application.insert_logs(application_id, results)) System.out.println("Logs successfully inserted.");
                                else System.out.println("Logs unsuccessfully inserted.");
                            }
                        }
                    }
                }
            }
            System.out.println("Post successfully read.");
        } else {
            System.out.println("Missing required headers.");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "A servlet listening for logs being sent to the server.";
    }// </editor-fold>

}
