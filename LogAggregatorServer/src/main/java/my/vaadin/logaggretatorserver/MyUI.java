package my.vaadin.logaggretatorserver;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Upload;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

    private final ServerDataBase vdb = new ServerDataBase(
            "localhost:3306/VaadinApp", 
            "root", 
            "root");
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        System.out.println("Snoop dogg: " + vaadinRequest);
        System.out.println("Tezd: ");
        String exempel = "dagensLogg.log";
        if (exempel.endsWith(".log") || exempel.endsWith(".log2")) {
            
        }
//    }
//        getPage().addUriFragmentChangedListener(
//            new UriFragmentChangedListener() {
//                public void uriFragmentChanged(
//                UriFragmentChangedEvent source) {
//                    enter(source.getUriFragment());
//                }
//            });
//        
//        enter(getPage().getUriFragment());
        final VerticalLayout vLayout = new VerticalLayout();
        final VerticalLayout vSubLayout = new VerticalLayout();
        final HorizontalLayout hLayout = new HorizontalLayout();
        final Grid grid = new Grid();
        
        vLayout.setMargin(true);
        hLayout.setMargin(true);
        vSubLayout.setMargin(true);
        setContent(vLayout);
        
        final TextField firstName = new TextField();
        firstName.setCaption("First name:");
        
        final TextField lastName = new TextField();
        lastName.setCaption("Last name:");
        
        final TextField brashibnik = new TextField();
        brashibnik.setCaption("Brashibnik:");
        
        vdb.connect();
        String[] test = {"first_name", "brashibnik", "last_name"};
        List<List<String>> results = vdb.select(test, "test_table");

        grid.setColumns(test);
        vdb.close();

        for (List<String> row: results) {
            grid.addRow(row.toArray());
        }
        
        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Object[][] tmpTest = {{firstName.getValue(), lastName.getValue(), brashibnik.getValue()}};
                vdb.connect();
                vdb.insert(test, tmpTest, "test_table");
                vdb.close();
                firstName.clear();
                lastName.clear();
                brashibnik.clear();
            }
        });

        vSubLayout.addComponents(firstName, lastName, brashibnik, button);
        hLayout.addComponents(grid, vSubLayout);
        vLayout.addComponent(hLayout);

        Upload uploadBtn = new Upload();
        vLayout.addComponent(uploadBtn);

        // A request handler for generating some content
        VaadinSession.getCurrent().addRequestHandler(
                new RequestHandler() {
            @Override
            public boolean handleRequest(VaadinSession session,
                                         VaadinRequest request,
                                         VaadinResponse response)
                    throws IOException {
                if ("/rhexample".equals(request.getPathInfo())) {
                    // Generate a plain text document
                    response.setContentType("text/plain");
                    response.getWriter().append(
                       "Here's some dynamically generated content.\n");
                    response.getWriter().format(Locale.ENGLISH,
                       "Time: %Tc\n", new Date());

                    // Use shared session data
                    response.getWriter().format("Session data: %s\n",
                        session.getAttribute("mydata"));

                    return true; // We wrote a response
                } else
                    return false; // No response was written
            }
        });
    }
//
//    void enter(String fragment) {
//        System.out.println(fragment);
//        final VerticalLayout vLayout = new VerticalLayout();
//        vLayout.setMargin(true);
//        setContent(vLayout);
//        if (fragment.equals("login")) {
//            Label lbl1 = new Label("login");
//            vLayout.addComponent(lbl1);
//        } else {
//            Label lbl1 = new Label("index");
//            vLayout.addComponent(lbl1);
//        }
//    }
    
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
