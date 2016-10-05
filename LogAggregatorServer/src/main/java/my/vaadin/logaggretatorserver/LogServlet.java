package my.vaadin.logaggretatorserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collections;
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
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LogServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LogServlet at " + request.getContextPath() + "</h1>");
            out.println("<form action=\"http://localhost:8080/LogAggregatorServer/LogServlet\" method=\"post\" enctype=\"multipart/form-data\">");
            out.println("<input type=\"file\" name=\"file\">");
//            out.println("<input type=\"text\" name=\"paramName\">");
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
        System.out.println(request.getHeader("content-type"));
        System.out.println(request.getHeader("content-length"));
        System.out.println("Post recieved.");

//        BufferedReader br = request.getReader();
//        String readLine;
//        while ((readLine = br.readLine()) != null) {
//            System.out.println("Read line: " + readLine);
//        }

        Part givenFile;
        if ((givenFile = request.getPart("file")) != null) {
            
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
                    System.out.println(line);
                    baos.write(buffer, 0, line);
                }

                File file = new File("./PackedLog.zip");
                FileOutputStream fop = new FileOutputStream(file);

                if (!file.exists()) file.createNewFile();
                fop.write(baos.toByteArray());
                fop.close();
            }
        }
        System.out.println("Post successfully read.");
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
