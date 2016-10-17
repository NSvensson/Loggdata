package my.vaadin.logaggretatorserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigServlet extends HttpServlet {

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
            out.println("<body>");
            out.println("<h1>Servlet ConfigServlet at " + request.getContextPath() + "</h1>");
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
        /*
        Headers to work with:
        username: the username to authenticate the user
        password: the password to authenticate the user

        config-option: this header will be used to know what action the 
                       client wishes to take.
                       
            Options:
                new_application - This option will be used when a new application is to be
                                  registered on the server.
                    Extra headers required for new_application:
                        interval - Integer representing the amount of seconds 
                                   inbetween every update.
                        app_name - The desired name for the new application 
                                   being added.
                
                get_config - This option will be used to obtain the configuration
                             for a registered application.
                    Extra headers required for get_config:
                        app_id - The id of the application the desired configuration
                                 belongs to.

        config-option:
            authenticate
                au_username
                au_password
                
        */
//        response.addHeader("test_header", "Snoop");
        System.out.println("Get request received.");
        System.out.println(Collections.list(request.getHeaderNames()));
        
        if (request.getHeader("config-option") != null) {
            //BEGIN AUTHENTICATION REQUEST
            if (request.getHeader("config-option").equals("authenticate") &&
                    request.getHeader("au_username") != null &&
                    request.getHeader("au_password") != null) {
                
                System.out.println("Authentication headers received.");
                
                ServerDataBase sdb = new ServerDataBase(
                        "localhost:3306/loggdatacollector",
                        "root",
                        "root");
                
                HashMap whereQuery = new HashMap();
                whereQuery.put("username", request.getHeader("au_username"));
                whereQuery.put("password", request.getHeader("au_password"));
                
                sdb.connect();
                String[][] select = sdb.select(new String[] {"id"}, "user", whereQuery);
                sdb.close();
                
                if (select != null && select.length > 0 && select[0][0] != null) {
                    System.out.println("Authenticated.");
                    response.addHeader("authenticated", "true");
                } else {
                    System.out.println("Invalid authentication.");
                    response.addHeader("authenticated", "false");
                }
            }
            //END AUTHENTICATION REQUEST
            
            //BEGIN NEW_APPLICATION REQUEST
            if (request.getHeader("config-option").equals("new_application") && 
                    request.getHeader("interval") != null &&
                    request.getHeader("app_name") != null) {
                
                
            }
            //END NEW_APPLICATION REQUEST
            
            //BEGIN GET_CONFIG REQUEST
            //END GET_CONFIG REQUEST
        }
        
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
        System.out.println("Post request received.");
        System.out.println(Collections.list(request.getHeaderNames()));
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "This servlet will deal with the configutations for each registered application.";
    }// </editor-fold>
}
