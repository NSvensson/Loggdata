package my.vaadin.logaggretatorserver;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component.Focusable;
import com.vaadin.ui.Grid;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Upload;
import java.awt.Component;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
    private final String loginView = "";
    private final String logsView = "Logs";
    
    private CurrentUser user;
    private Navigator nav = new Navigator(this, this);

    private String[][] tmpTestdata = {
        {"Firefox", "2016-01-01", "SNOOP DOGG WAS HERE"},
        {"Firefox", "2016-02-20", "SNOOasdasdP DOGG WAS HERE"},
        {"Firefox", "2016-03-20", "SNOOP DOGG WAS HERE"},
        {"Firefox", "2016-04-20", "SNOOP DasdasdOGG WAS HERE"},
        {"Firefox", "2016-05-20", "SNOOP DOGG WAS HERE"},
        {"Firefox", "2016-06-20", "SNOasdasdOP DOGGasdasd asdasdWAS HERE"},
        {"Firefox", "2016-07-20", "SNOOP DOGG WAS asdasdHERE"},
        {"Firefox", "2016-08-20", "SNOOP DOasdasdGG WAS HERE"},
        {"Chrome", "2016-08-20", "SNOOP DOGG WAS HERE"},
        {"Chrome", "2016-10-20", "SNOOP DasdasdOGG WAS HERE"},
        {"Chrome", "2017-11-20", "SNOOP DOGG WASasdasd HERE"},
        {"Chrome", "2016-12-20", "SNOOP DOGG WAS HERE"},
        {"Chrome", "2016-10-19", "SNOOP DOGG WAS HERE"},
        {"Chrome", "2016-10-20", "SNOOP DOasdasdGG WAS HERE"},
        {"Chrome", "2016-10-21", "SNOOP DOGG WAS HERE"},
        {"Chrome", "2016-10-20", "SNOOP DOasdasdGG WAS HERE"},
        {"Chrome", "2016-10-20", "SNOOP DOGG WAS HERE"},
        {"Chrome", "2016-10-25", "SNOOP DOGG WAS HERE"},
        {"IE", "2016-10-20", "SNOOP DOGG WAS HERE"},
        {"IE", "2016-10-21", "SNOOP DOGG WAS HERE"},
        {"IE", "2016-11-20", "SNOOP DOGG WASasdasd HERE"},
        {"IE", "2016-10-20", "SNOOP DOGG WAS HERE"},
        {"IE", "2016-10-10", "BRASHIBNIK"},
        {"IE", "2016-10-20", "SNOOP DOGG WAS HERE"},
        {"IE", "2016-09-20", "SNOOP DOGG WAS HERE"}
    };
    
    HashMap<String, String> tmpTestApps = new HashMap<String, String>();
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        
        tmpTestApps.put("1", "Firefox");
        tmpTestApps.put("2", "Chrome");
        tmpTestApps.put("3", "IE");
        
        getPage().setTitle("Log server");
        
        nav.addView(loginView, new LoginLayout());
        nav.addView(logsView, new ViewLogsLayout());
    }
    
    //LoginLayout start
    public class LoginLayout extends GridLayout implements View {
        public LoginLayout() {
            final GridLayout layout = new GridLayout(1,4);
            setWidth("100%");
            setHeight("100%");
            layout.addStyleName("login-grid-layout");
            layout.setSpacing(true);

            addComponent(layout);
            setComponentAlignment(layout, Alignment.MIDDLE_CENTER);

            Label start = new Label("Login");
            layout.addComponent(start, 0, 0);
            layout.setComponentAlignment(start, Alignment.MIDDLE_CENTER);

            TextField usernametxf = new TextField("Username");
            layout.addComponent(usernametxf,0,1);
            layout.setComponentAlignment(usernametxf, Alignment.TOP_LEFT);

            PasswordField passwordpwf = new PasswordField("Password");
            layout.addComponent(passwordpwf,0,2);
            layout.setComponentAlignment(passwordpwf, Alignment.TOP_LEFT);

            Button loginbtn = new Button("Login");
            loginbtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    user = new CurrentUser(
                            usernametxf.getValue(),
                            passwordpwf.getValue());
                    
                    System.out.println("Username provided: " + usernametxf.getValue());
                    System.out.println("Password provided: " + passwordpwf.getValue());
                    
                    if (user.is_authenticated) {
                        System.out.println("User authenticated.");
                        nav.navigateTo(logsView);
                    } else {
                        System.out.println("Invalid credentials provided.");
                    }
                }
            });
            layout.addComponent(loginbtn,0,3);
        }
        
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            System.out.println("LoginLayout entered.");
        }
    }
    //LoginLayout end
        
    //ViewLogsLayout start
    public class ViewLogsLayout extends GridLayout implements View {
        public ViewLogsLayout() {
            setWidth("100%");
            setHeight("100%");

            final GridLayout loglayout = new GridLayout(1,4);
//            final VerticalLayout loglayout = new VerticalLayout();
            loglayout.setWidth("90%");
            loglayout.setHeight("90%");
            addComponent(loglayout);
            setComponentAlignment(loglayout, Alignment.MIDDLE_CENTER);
            
            final GridLayout gTitleLayout = new GridLayout(2,1);
            Label testLbl = new Label("Your log.");
            testLbl.addStyleName("title_padding");
            gTitleLayout.addComponent(testLbl,0,0);
//            vTitleLayout.addComponent(testLbl);
            
            Grid logtable = new Grid();
            logtable.setSizeFull();
            logtable.setColumns(new String[] {"Name", "Date", "Event"});
            Grid.Column cName = logtable.getColumn("Name");
            Grid.Column cDate = logtable.getColumn("Date");
            cName.setWidth(200);
            cDate.setWidth(200);
            
            for (String[] row : tmpTestdata) {
                logtable.addRow(row);
            }

            String nonSpecifiedOption = "All applications";
            ComboBox app_name = new ComboBox("Applications");
            app_name.addItem(nonSpecifiedOption);
            app_name.setValue(nonSpecifiedOption);
            app_name.addItems(tmpTestApps.values());
            app_name.setNullSelectionAllowed(false);
            app_name.setTextInputAllowed(false);
            app_name.addValueChangeListener(new ComboBox.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    String tmpValue = (String) app_name.getValue();
                    
                    if (tmpValue.equals(nonSpecifiedOption)) {
                        for (String[] row : tmpTestdata) {
                            logtable.addRow(row);
                        }
                    } else {
                        for (String[] row : tmpTestdata) {
                            if (row[0].equals(tmpValue)) logtable.addRow(row);
                        }
                    }
                }
            });
            
            
//            loglayout.addComponent(app_name, 0, 1);
            gTitleLayout.addComponent(app_name,1,0);
            gTitleLayout.setComponentAlignment(app_name, Alignment.BOTTOM_RIGHT);
            gTitleLayout.addStyleName("apps_padding");
            loglayout.addComponent(gTitleLayout,0,0);
            
//            loglayout.addComponent(logtable,0,2);
            loglayout.addComponent(logtable);
            
            HorizontalLayout buttonLayout = new HorizontalLayout();
            Button out = new Button("Logout");
            out.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    nav.navigateTo(loginView);
                    user = null;
                }
            });
            buttonLayout.addComponent(out);
            buttonLayout.setComponentAlignment(out, Alignment.TOP_RIGHT);
            
//            loglayout.addComponent(buttonLayout, 0, 3);
            loglayout.addComponent(buttonLayout);
            loglayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
            
            loglayout.setRowExpandRatio(1,1);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            System.out.println("ViewLogsLayout entered.");
        }
    }
    //ViewLogsLayout end
    
    
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
