package my.vaadin.logaggretatorserver;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

        if (request.getHeader("client-action") != null) {
            
            //Authentication request begin.
            if (request.getHeader("client-action").equals("authenticate") &&
                request.getHeader("username") != null &&
                request.getHeader("password") != null) {
                
                /*
                When sending a authentication request to the server
                the following requirements needs to be met:
                
                The header "client-action" with the value "authenticate".
                
                The header "username" containing the username for the user
                getting authenticated.
                
                The header "password" containing the pre hashed/encrypted
                password for the user getting authenticated.
                Warning: The hashing/encryption method on the client side
                must share the same hashing/encryption key as the server.
                */
                
                /*
                Create a CurrentUser object with the provided information.

                Providing the boolean false as the third parameter will prevent
                the object from reading and filling the memory with all the 
                applications and logs related to the user.
                */
                CurrentUser user = new CurrentUser(
                        request.getHeader("username"),
                        request.getHeader("password"),
                        false);
                
                if (user.applications == null) System.out.println("Applications not loaded.");
                else System.out.println("Applications was loaded.");
                
                /*
                Check if the user information provided is valid.
                */
                if (user.is_authenticated) {
                    /*
                    Check if the user has rights to use the client.
                    */
                    if (user.user_group.manage_applications) {
                        System.out.println("Authentication: Authenticated.");
                        response.addHeader("authenticated", "true");
                    } else {
                        System.out.println("Authentication: Permission denied.");
                        response.addHeader("authenticated", "false");
                        response.addHeader("authentication-problem", "Permission denied.");
                    }
                } else {
                    System.out.println("Authentication: Invalid authentication information.");
                    response.addHeader("authenticated", "false");
                    response.addHeader("authentication-problem", "Invalid authentication information.");
                }
            }
            //Authentication request end.

            //New_application request begin.
            if (request.getHeader("client-action").equals("new_application") &&
                request.getHeader("application-name") != null &&
                request.getHeader("update-interval") != null &&
                request.getHeader("username") != null &&
                request.getHeader("password") != null) {
                
                /*
                When attempting to add a new application on the server
                the following requirements needs to be met:
                
                The header "client-action" with the value "new_application".
                
                The header "application-name" containing the desired name
                for the application being added.
                
                The header "update-interval" containing the amount of seconds
                of each interval between each time the client is updating the
                logs on the server.
                
                The header "username" containing the username for the user
                getting authenticated.
                
                The header "password" containing the pre hashed/encrypted
                password for the user getting authenticated.
                Warning: The hashing/encryption method on the client side
                must share the same hashing/encryption key as the server.
                */
                
                CurrentUser user = new CurrentUser(
                        request.getHeader("username"),
                        request.getHeader("password"),
                        false);
                
                Administration administration = new Administration(user.user_group);
                
                String api_key = administration.application.create(
                        user.company.id,
                        request.getHeader("application-name"),
                        null,
                        request.getHeader("update-interval"));

                if (api_key != null) {
                    System.out.println("New application created.");
                    response.addHeader("application-added", "true");
                    
                    response.addHeader("api-key", api_key);
                } else {
                    System.out.println("New application not created.");
                    response.addHeader("application-added", "false");
                    
                    if (administration.application.settings.NAME_ERROR_MESSAGE != null)
                        response.addHeader("application-name-problem", administration.application.settings.NAME_ERROR_MESSAGE);

                    if (administration.application.settings.UPDATE_INTERVAL_ERROR_MESSAGE != null)
                        response.addHeader("update-interval-problem", administration.application.settings.UPDATE_INTERVAL_ERROR_MESSAGE);
                }
            }
            //New_application request end.
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
            Administration administration = new Administration(new UserGroups(true));
            
            ApplicationRow application = administration.application.authenticate_API_key(request.getHeader("api-key"));
            
            Part given_file;
            if (application != null && (given_file = request.getPart("file")) != null) {
                System.out.println("API key is valid.");
                
                response.addHeader("api-authenticated", "true");
                response.addHeader("application-name", application.name);
                response.addHeader("update-interval", application.update_interval);

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
                            
                            ArrayList<String[]> results_array_list = new ArrayList<>();
                            
                            BufferedReader br = new BufferedReader(new InputStreamReader(zip_input_stream));
                            
                            String read_line;
                            while ((read_line = br.readLine()) != null) {
                                
                                Matcher matcher = pattern.matcher(read_line);
                                
                                if (matcher.find()) {
                                    String tmp_date = matcher.group(0);
                                    String tmp_event = read_line.replaceAll(tmp_date, "");
                                    
                                    results_array_list.add(new String[] { tmp_date, tmp_event });
                                } else  {
                                    if (!results_array_list.isEmpty() && read_line != null && read_line.length() >= 1) {
                                        String[] tmp_string_array = new String[2];
                                        tmp_string_array[0] = results_array_list.get(results_array_list.size() - 1)[0];
                                        tmp_string_array[1] = results_array_list.get(results_array_list.size() - 1)[1] + "\n" + read_line;
                                        
                                        results_array_list.set(results_array_list.size() - 1, tmp_string_array);
                                    }
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
                                if (administration.application.insert_logs(application.id, results)) System.out.println("Logs successfully inserted.");
                                else System.out.println("Logs unsuccessfully inserted.");
                            }
                        }
                    }
                } else {
                    System.out.println("API key doesn't exist.");
                    
                    response.addHeader("api-authenticated", "false");
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
        return "A servlet used for all interactions between the log server and the log clients.";
    }// </editor-fold>
}