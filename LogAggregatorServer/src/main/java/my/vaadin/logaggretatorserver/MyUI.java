package my.vaadin.logaggretatorserver;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Upload;
import java.awt.Component;
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
    


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        final GridLayout main = new GridLayout();
        final GridLayout layout = new GridLayout(2,6);
        main.setWidth("100%");
        main.setHeight("100%");
        
        main.addComponent(layout);
        main.setComponentAlignment(layout, Alignment.MIDDLE_CENTER);
        
        layout.addComponent(new Label("Username"),1,1);
        layout.addComponent(new TextField(""),1,2);
        
        layout.addComponent(new Label("Password"),1,3);
        layout.addComponent(new PasswordField(""),1,4);
        
        layout.addComponent(new Button("Login"),1,5);
        
        
        
        setContent(main);
        
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
