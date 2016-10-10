package my.vaadin.logaggretatorserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
public class LogServlet extends HttpServlet {

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
        
        System.out.println(Collections.list(request.getHeaderNames()));
        System.out.println(request.getHeader("app-id"));
        System.out.println(request.getHeader("api-key"));
        System.out.println("Post recieved.");

        if (request.getHeader("app-id") != null && request.getHeader("api-key") != null) {
            ServerDataBase sdb = new ServerDataBase(
                    "localhost:3306/logtestdb",
                    "root",
                    "root");

            HashMap where = new HashMap();
            where.put("app_id", request.getHeader("app-id"));

            sdb.connect();
            String[][] api_key = sdb.select(
                    new String[] {"api_key"},
                    "config",
                    where);
            sdb.close();

            Part givenFile;
            if (api_key != null && api_key[0][0].equals(request.getHeader("api-key")) && (givenFile = request.getPart("file")) != null) {
                System.out.println("API key correct.");

                String contentType = givenFile.getContentType();
                if (contentType != null && (
                        contentType.contains("application/zip") || 
                        contentType.contains("application/octet-stream") || 
                        contentType.contains("application/x-zip-compressed")
                        )) {

                    InputStream sis = givenFile.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[sis.available()];

                    int line = -1;
                    while ((line = sis.read(buffer)) != -1) {
                        baos.write(buffer, 0, line);
                    }

                    byte[] read_file = baos.toByteArray();

                    /*
                    The first 4 bytes can be used to determine whether the read file
                    is a zip or not. The 4 magic numbers to determine if it is a zip
                    file is: 80 75 3 4.
                    */
                    if (read_file.length >= 4 && 
                            read_file[0] == 80 && 
                            read_file[1] == 75 && 
                            read_file[2] == 3 && 
                            read_file[3] == 4) {

                        File file = new File("./ReceivedLog.zip");
                        try (FileOutputStream fop = new FileOutputStream(file)) {
                            if (!file.exists()) file.createNewFile();
                            fop.write(read_file);
                            System.out.println("File successfully transfered.");
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
