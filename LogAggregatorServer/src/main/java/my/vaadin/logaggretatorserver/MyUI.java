package my.vaadin.logaggretatorserver;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;


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
    private final String createUserView = "CreateUser";
    private final String editUserView = "EditUser";
    private final String createCompanyView = "CreateCompany";
    private final String manageUsersView = "ManageUsers";
    private final String manageCompaniesView = "ManageCompanies";
    
    private CurrentUser user;
    private CurrentUser selectedUser = null;
    private Navigator nav = new Navigator(this, this);
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Configurations.readPropertiesFile();
        
        getPage().setTitle("Log server");
        
        nav.addView(loginView, new LoginLayout());
        nav.addView(logsView, new ViewLogsLayout());
        nav.addView(createUserView, new CreateUserLayout());
        nav.addView(createCompanyView, new CreateCompanyLayout());
        nav.addView(manageUsersView, new ManageUsersLayout());
        nav.addView(editUserView, new EditUserLayout());
        nav.addView(manageCompaniesView, new ManageCompanyLayout());
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
                            DataManaging.hashString(passwordpwf.getValue()));
                    
                    if (user.is_authenticated) {
                        System.out.println("User authenticated.");
                        nav.navigateTo(logsView);
                    } else {
                        Notification.show("Invalid credentials provided.",
                        Notification.TYPE_ERROR_MESSAGE);
                        System.out.println("Invalid credentials provided.");
                    }
                }
            });
            layout.addComponent(loginbtn,0,3);
        }
        
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            //LoginLayout entered.
        }
    }
    //LoginLayout end
    
    //ViewLogsLayout start
    public class ViewLogsLayout extends GridLayout implements View {
        
        public final String HIDDEN_COLUMN_IDENTIFIER = "id";
        public final String APPLICATION_NAME_COLUMN_IDENTIFIER = "Application";
        public final String DATE_COLUMN_NAME_IDENTIFIER = "Date";
        public final String EVENT_COLUMN_NAME_IDENTIFIER = "Event";
        
        public final IndexedContainer tableContainer = new IndexedContainer();
        public final IndexedContainer comboBoxContainer = new IndexedContainer();
        public final Grid logtable = new Grid(tableContainer);
        public final ComboBox app_name = new ComboBox("Applications", comboBoxContainer);
        public final Button manage_users = new Button("Manage users");
        public final Button manage_companies = new Button("Manage companies");
        
        public ViewLogsLayout() {
            
            this.tableContainer.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.APPLICATION_NAME_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.DATE_COLUMN_NAME_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.EVENT_COLUMN_NAME_IDENTIFIER, String.class, null);
            
            this.logtable.getColumn(this.HIDDEN_COLUMN_IDENTIFIER).setHidden(true);
            this.logtable.getColumn(this.APPLICATION_NAME_COLUMN_IDENTIFIER).setWidth(200);
            this.logtable.getColumn(this.DATE_COLUMN_NAME_IDENTIFIER).setWidth(200);
            
            setWidth("100%");
            setHeight("100%");

            final GridLayout loglayout = new GridLayout(1,4);
            loglayout.setWidth("90%");
            loglayout.setHeight("90%");
            addComponent(loglayout);
            setComponentAlignment(loglayout, Alignment.MIDDLE_CENTER);

            final GridLayout gTitleLayout = new GridLayout(2,1);
            gTitleLayout.setStyleName("titel_padding");
            Label testLbl = new Label("Your log.");
            gTitleLayout.addComponent(testLbl,0,0);

            this.logtable.setSizeFull();

            this.comboBoxContainer.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.comboBoxContainer.addContainerProperty(this.APPLICATION_NAME_COLUMN_IDENTIFIER, String.class, null);
            
            this.app_name.setItemCaptionPropertyId(APPLICATION_NAME_COLUMN_IDENTIFIER);
            this.app_name.setNullSelectionAllowed(false);
            this.app_name.setTextInputAllowed(false);
            this.app_name.addValueChangeListener(new ComboBox.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    if (user != null && user.is_authenticated && user.applications != null) {
                        String tmpValue = app_name.getContainerProperty(app_name.getValue(), HIDDEN_COLUMN_IDENTIFIER).getValue().toString();
                        if (tableContainer.hasContainerFilters()) tableContainer.removeAllContainerFilters();
                        if (!tmpValue.equals("0")) {
                            tableContainer.addContainerFilter(HIDDEN_COLUMN_IDENTIFIER, tmpValue, true, true);
                        }
                    }
                }
            });

            gTitleLayout.addComponent(this.app_name,1,0);
            gTitleLayout.setComponentAlignment(this.app_name, Alignment.BOTTOM_RIGHT);
            gTitleLayout.addStyleName("apps_padding");
            loglayout.addComponent(gTitleLayout,0,0);

            loglayout.addComponent(this.logtable,0,1);

            HorizontalLayout buttonLayout = new HorizontalLayout();
            Button out = new Button("Logout");
            out.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    tableContainer.removeAllItems();
                    comboBoxContainer.removeAllItems();
                    manage_users.setVisible(false);
                    manage_companies.setVisible(false);
                    user = null;
                    nav.navigateTo(loginView);
                }
            });
            
            buttonLayout.addComponent(out);
            buttonLayout.setComponentAlignment(out, Alignment.TOP_RIGHT);
            
            loglayout.addComponent(buttonLayout,0,2);
            loglayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
            
            HorizontalLayout administrationLayout = new HorizontalLayout();
            manage_users.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    tableContainer.removeAllItems();
                    comboBoxContainer.removeAllItems();
                    nav.navigateTo(manageUsersView);
                }
            });
            manage_companies.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){
                    tableContainer.removeAllItems();
                    comboBoxContainer.removeAllItems();
                    nav.navigateTo(manageCompaniesView);
                }
            });
            
            manage_users.setEnabled(false);
            manage_users.setVisible(false);
            manage_companies.setEnabled(false);
            manage_companies.setVisible(false);
            administrationLayout.addComponent(manage_users);
            administrationLayout.addComponent(manage_companies);
            loglayout.addComponent(administrationLayout,0,3);
            
            loglayout.setRowExpandRatio(1,1);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            if (user != null && user.is_authenticated) {
                
                String nonSpecifiedOption = "All applications";
                Object standardChoiceId = comboBoxContainer.addItem();
                Item standardChoice = comboBoxContainer.getItem(standardChoiceId);
                standardChoice.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue("0");
                standardChoice.getItemProperty(this.APPLICATION_NAME_COLUMN_IDENTIFIER).setValue(nonSpecifiedOption);
                this.app_name.setValue(standardChoiceId);
                
                if (user.applications != null) {
                    
                    for (ApplicationRow application : user.applications) {
                        
                        if (application.logs != null) {
                            for (LogRow log : application.logs) {
                                Item newLog = tableContainer.getItem(tableContainer.addItem());
                                newLog.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(application.id);
                                newLog.getItemProperty(this.APPLICATION_NAME_COLUMN_IDENTIFIER).setValue(application.name);
                                newLog.getItemProperty(this.DATE_COLUMN_NAME_IDENTIFIER).setValue(log.date);
                                newLog.getItemProperty(this.EVENT_COLUMN_NAME_IDENTIFIER).setValue(log.event);
                            }
                            Item newChoice = comboBoxContainer.getItem(comboBoxContainer.addItem());
                            newChoice.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(application.id);
                            newChoice.getItemProperty(this.APPLICATION_NAME_COLUMN_IDENTIFIER).setValue(application.name);
                        }
                    }
                }
                
                if (user.user_group.manage_users) {
                    if (!manage_users.isVisible()) manage_users.setVisible(true);
                    if (!manage_users.isEnabled()) manage_users.setEnabled(true);
                }
                if (user.user_group.manage_companies) {
                    if (!manage_companies.isVisible()) manage_companies.setVisible(true);
                    if (!manage_companies.isEnabled()) manage_companies.setEnabled(true);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //ViewLogsLayout end
    
    //ManageUsersLayout start
    public class ManageUsersLayout extends GridLayout implements View {
        
        public Administration administration = new Administration();
        
        public final String HIDDEN_COLUMN_IDENTIFIER = "id";
        public final String FIRST_NAME_COLUMN_IDENTIFIER = "Name";
        public final String LAST_NAME_NAME_COLUMN_IDENTIFIER = "Lastname";
        public final String EMAIL_COLUMN_IDENTIFIER = "Email";
        public final String USERNAME_COLUMN_IDENTIFIER = "Username";
        public final String USER_GROUP_COLUMN_IDENTIFIER = "User group";
        public final String USER_COMPANY_COLUMN_IDENTIFIER = "User company";
        
        public final IndexedContainer tableContainer = new IndexedContainer();
        public final Grid usertable = new Grid(tableContainer);
        
        public ManageUsersLayout(){
            
            this.tableContainer.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.FIRST_NAME_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.LAST_NAME_NAME_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.EMAIL_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.USERNAME_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.USER_GROUP_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.USER_COMPANY_COLUMN_IDENTIFIER, String.class, null);
            
            this.usertable.getColumn(this.HIDDEN_COLUMN_IDENTIFIER).setHidden(true);
            this.usertable.setSelectionMode(SelectionMode.SINGLE);
            
            setWidth("100%");
            setHeight("100%");

            final GridLayout userlayout = new GridLayout(1,4);
            userlayout.setWidth("90%");
            userlayout.setHeight("90%");
            addComponent(userlayout);
            setComponentAlignment(userlayout, Alignment.MIDDLE_CENTER);

            Label testLbl = new Label("Users.");
            userlayout.addComponent(testLbl,0,0);
            userlayout.addComponent(usertable, 0, 1);
            userlayout.setColumnExpandRatio(0, 1);
            userlayout.setRowExpandRatio(1,1);
            usertable.setSizeFull();
            
            //Layout for the buttons
            HorizontalLayout buttonLayout = new HorizontalLayout();
            
            Button back = new Button("Back");
            back.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    tableContainer.removeAllItems();
                    nav.navigateTo(logsView);
                }
            });
            
            Button createUser = new Button("Create user");
            createUser.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    tableContainer.removeAllItems();
                    nav.navigateTo(createUserView);
                }
            });
            buttonLayout.addComponent(createUser);
            buttonLayout.addComponent(back);

            Button editUserButton = new Button("Edit user");
            editUserButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) usertable.getSelectionModel()).getSelectedRow();

                    if (selected != null) {
                        selectedUser = new CurrentUser(usertable.getContainerDataSource().getItem(selected).getItemProperty(HIDDEN_COLUMN_IDENTIFIER).getValue().toString());
                        System.out.println("Selected user id: " + selectedUser.id);
                        tableContainer.removeAllItems();
                        nav.navigateTo(editUserView);
                    } else {
                        System.out.println("Nothing selected.");
                    }
                }
            });

            buttonLayout.addComponent(editUserButton);
            userlayout.addComponent(buttonLayout, 0, 2);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_users) {

                    administration = new Administration(user.user_group);
                    
                    for (CurrentUser userRow : administration.object_collections.users()) {
                        Item userItem = tableContainer.getItem(tableContainer.addItem());
                        userItem.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(userRow.id);
                        userItem.getItemProperty(this.FIRST_NAME_COLUMN_IDENTIFIER).setValue(userRow.first_name);
                        userItem.getItemProperty(this.LAST_NAME_NAME_COLUMN_IDENTIFIER).setValue(userRow.last_name);
                        userItem.getItemProperty(this.EMAIL_COLUMN_IDENTIFIER).setValue(userRow.email);
                        userItem.getItemProperty(this.USERNAME_COLUMN_IDENTIFIER).setValue(userRow.username);
                        userItem.getItemProperty(this.USER_COMPANY_COLUMN_IDENTIFIER).setValue(userRow.company.name);
                        userItem.getItemProperty(this.USER_GROUP_COLUMN_IDENTIFIER).setValue(userRow.user_group.name);
                    }
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //ManageUsersLayout end
    
    //CreateUserLayout start
    public class CreateUserLayout extends GridLayout implements View {

        public final String HIDDEN_COLUMN_IDENTIFIER = "id";
        public final String NAME_COLUMN_IDENTIFIER = "Name";

        public Administration administration = new Administration();
        
        public final IndexedContainer company_container = new IndexedContainer();
        public final IndexedContainer user_group_container = new IndexedContainer();
        
        public final ComboBox company_name = new ComboBox("Company", company_container);
        public final ComboBox user_group_name = new ComboBox("User group", user_group_container);

        public TextField userFnameField = new TextField("First name");
        public TextField userLnameField = new TextField("Last name");
        public TextField userEmailField = new TextField("Email");
        public TextField userUnameField = new TextField("Username");
        public PasswordField userPwordField = new PasswordField("Password");
        public PasswordField userCPwordField = new PasswordField("Confirm password");
        
        public Label first_name_label = new Label();
        public Label last_name_label = new Label();
        public Label email_label = new Label();
        public Label username_label = new Label();
        public Label password_label = new Label();
        public Label confirm_password_label = new Label();
        public Label company_label = new Label();
        public Label user_group_label = new Label();
        
        public CreateUserLayout() {
            
            this.company_container.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.company_container.addContainerProperty(this.NAME_COLUMN_IDENTIFIER, String.class, null);
            
            this.user_group_container.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.user_group_container.addContainerProperty(this.NAME_COLUMN_IDENTIFIER, String.class, null);
            
            this.company_name.setNullSelectionAllowed(false);
            this.company_name.setTextInputAllowed(false);
            
            this.user_group_name.setNullSelectionAllowed(false);
            this.user_group_name.setTextInputAllowed(false);
            
            this.company_name.setItemCaptionPropertyId(this.NAME_COLUMN_IDENTIFIER);
            this.user_group_name.setItemCaptionPropertyId(this.NAME_COLUMN_IDENTIFIER);
            
            this.first_name_label.setVisible(false);
            this.last_name_label.setVisible(false);
            this.email_label.setVisible(false);
            this.username_label.setVisible(false);
            this.password_label.setVisible(false);
            this.confirm_password_label.setVisible(false);
            this.company_label.setVisible(false);
            this.user_group_label.setVisible(false);
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout createUserLayout = new GridLayout(1,10);
            createUserLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            //Fields and components for the  layout.
            Label createUserTitle = new Label("Create user");
            
            Button back = new Button("Back");
            back.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    company_container.removeAllItems();
                    user_group_container.removeAllItems();
                    nav.navigateTo(manageUsersView);
                }
            });
            Button createUser = new Button("Create");
            createUser.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){
                    administration.user.settings.clear_error_messages();
                    
                    first_name_label.setVisible(false);
                    last_name_label.setVisible(false);
                    email_label.setVisible(false);
                    username_label.setVisible(false);
                    password_label.setVisible(false);
                    confirm_password_label.setVisible(false);
                    company_label.setVisible(false);
                    user_group_label.setVisible(false);
                    
                    if (userPwordField.getValue().equals(userCPwordField.getValue())) {
                        if (administration.user.create(
                                userFnameField.getValue(),
                                userLnameField.getValue(),
                                userEmailField.getValue(),
                                userUnameField.getValue(),
                                userPwordField.getValue(),
                                company_name.getContainerProperty(company_name.getValue(), HIDDEN_COLUMN_IDENTIFIER).getValue().toString(),
                                user_group_name.getContainerProperty(user_group_name.getValue(), HIDDEN_COLUMN_IDENTIFIER).getValue().toString())) {
                            System.out.println("User created.");
                            Notification.show("  User created  ",
                            Notification.TYPE_HUMANIZED_MESSAGE);
                        } else {
                            System.out.println("User not created.");
                            
                            if (administration.user.settings.FIRST_NAME_ERROR_MESSAGE != null) {
                                first_name_label.setValue(administration.user.settings.FIRST_NAME_ERROR_MESSAGE);
                                if (!first_name_label.isVisible()) first_name_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.LAST_NAME_ERROR_MESSAGE != null) {
                                last_name_label.setValue(administration.user.settings.LAST_NAME_ERROR_MESSAGE);
                                if (!last_name_label.isVisible()) last_name_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.EMAIL_ERROR_MESSAGE != null) {
                                email_label.setValue(administration.user.settings.EMAIL_ERROR_MESSAGE);
                                if (!email_label.isVisible()) email_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.USERNAME_ERROR_MESSAGE != null) {
                                username_label.setValue(administration.user.settings.USERNAME_ERROR_MESSAGE);
                                if (!username_label.isVisible()) username_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.PASSWORD_ERROR_MESSAGE != null) {
                                password_label.setValue(administration.user.settings.PASSWORD_ERROR_MESSAGE);
                                if (!password_label.isVisible()) password_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.COMPANY_ERROR_MESSAGE != null) {
                                company_label.setValue(administration.user.settings.COMPANY_ERROR_MESSAGE);
                                if (!company_label.isVisible()) company_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.USER_GROUP_ERROR_MESSAGE != null) {
                                user_group_label.setValue(administration.user.settings.USER_GROUP_ERROR_MESSAGE);
                                if (!user_group_label.isVisible()) user_group_label.setVisible(true);
                            }
                        }
                    } else {
                        confirm_password_label.setValue("The provided passwords do not match.");
                        if (!confirm_password_label.isVisible()) confirm_password_label.setVisible(true);
                    }
                }
            });
            createUser.setStyleName("titel_padding");
            
            GridLayout bLayout = new GridLayout(2,1);
            bLayout.setStyleName("top_padding");
            bLayout.addComponent(createUser,0,0);
            bLayout.setComponentAlignment(createUser, Alignment.MIDDLE_LEFT);
            bLayout.addComponent(back,1,0);
            bLayout.setComponentAlignment(back, Alignment.MIDDLE_RIGHT);
            bLayout.setWidth("100%");
            bLayout.setHeight("100%");
            
            //Adding the fields to the layout.
            createUserLayout.addComponent(createUserTitle, 0, 0);
            createUserLayout.addComponent(this.userFnameField, 0, 1);
            createUserLayout.addComponent(this.userLnameField, 0, 2);
            createUserLayout.addComponent(this.userEmailField, 0, 3);
            createUserLayout.addComponent(this.userUnameField, 0, 4);
            createUserLayout.addComponent(this.userPwordField, 0, 5);
            createUserLayout.addComponent(this.userCPwordField, 0, 6);
            createUserLayout.addComponent(this.company_name, 0, 7);
            createUserLayout.addComponent(this.user_group_name, 0, 8);
            
            createUserLayout.addComponent(bLayout, 0, 9);
            
            
            //Label layout for the side of the view to display error messeges
            GridLayout labelLayout = new GridLayout(1,10);
            labelLayout.addComponent(this.user_group_label, 0, 8);
            labelLayout.setComponentAlignment(this.user_group_label, Alignment.MIDDLE_LEFT);
            labelLayout.addComponent(this.company_label, 0, 7);
            labelLayout.setComponentAlignment(this.company_label, Alignment.MIDDLE_LEFT);
            labelLayout.addComponent(this.confirm_password_label, 0, 6);
            labelLayout.setComponentAlignment(this.confirm_password_label, Alignment.MIDDLE_LEFT);
            labelLayout.addComponent(this.password_label, 0, 5);
            labelLayout.setComponentAlignment(this.password_label, Alignment.MIDDLE_LEFT);
            labelLayout.addComponent(this.username_label, 0, 4);
            labelLayout.setComponentAlignment(this.username_label, Alignment.MIDDLE_LEFT);
            labelLayout.addComponent(this.email_label, 0, 3);
            labelLayout.setComponentAlignment(this.email_label, Alignment.MIDDLE_LEFT);
            labelLayout.addComponent(this.last_name_label, 0, 2);
            labelLayout.setComponentAlignment(this.last_name_label, Alignment.MIDDLE_LEFT);
            labelLayout.addComponent(this.first_name_label, 0, 1);
            labelLayout.setComponentAlignment(this.first_name_label, Alignment.MIDDLE_LEFT);
//            addComponent(labelLayout);

            labelLayout.setHeight("100%");
            labelLayout.setWidth("100%");
            mainlayout.addComponent(createUserLayout);
            mainlayout.setComponentAlignment(createUserLayout, Alignment.MIDDLE_RIGHT);
            mainlayout.addComponent(labelLayout);
            mainlayout.setComponentAlignment(labelLayout, Alignment.MIDDLE_LEFT);
        }
        
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_users) {
                    company_name.removeAllItems();
                    user_group_name.removeAllItems();
                    
                    administration = new Administration(user.user_group);
                    
                    for (CompanyRow company : administration.object_collections.companies()) {
                        Item newItem = company_container.getItem(company_container.addItem());
                        newItem.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(company.id);
                        newItem.getItemProperty(this.NAME_COLUMN_IDENTIFIER).setValue(company.name);
                    }
                    
                    for (UserGroups group : administration.object_collections.user_groups()) {
                        Item newItem = user_group_container.getItem(user_group_container.addItem());
                        newItem.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(group.id);
                        newItem.getItemProperty(this.NAME_COLUMN_IDENTIFIER).setValue(group.name);
                    }
                    
                    company_name.setValue(company_name.getItemIds().toArray()[0]);
                    user_group_name.setValue(user_group_name.getItemIds().toArray()[0]);
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //CreateUserLayout end
    
    //EditUserLayout start
    public class EditUserLayout extends GridLayout implements View {

        public final String NAME_COLUMN_IDENTIFIER = "Name";

        public Administration administration = new Administration();
        
        public final IndexedContainer company_container = new IndexedContainer();
        public final IndexedContainer user_group_container = new IndexedContainer();
        
        public final ComboBox company_name = new ComboBox("Company", company_container);
        public final ComboBox user_group_name = new ComboBox("User group", user_group_container);

        public TextField userFnameField = new TextField("First name");
        public TextField userLnameField = new TextField("Last name");
        public TextField userEmailField = new TextField("Email");
        public TextField userUnameField = new TextField("Username");
        public PasswordField userPwordField = new PasswordField("Password");
        public PasswordField userCPwordField = new PasswordField("Confirm password");
        
        public Label first_name_label = new Label();
        public Label last_name_label = new Label();
        public Label email_label = new Label();
        public Label username_label = new Label();
        public Label password_label = new Label();
        public Label confirm_password_label = new Label();
        public Label company_label = new Label();
        public Label user_group_label = new Label();
        
        public EditUserLayout() {
            
            this.company_container.addContainerProperty(this.NAME_COLUMN_IDENTIFIER, String.class, null);
            
            this.user_group_container.addContainerProperty(this.NAME_COLUMN_IDENTIFIER, String.class, null);
            
            this.company_name.setNullSelectionAllowed(false);
            this.company_name.setTextInputAllowed(false);
            
            this.user_group_name.setNullSelectionAllowed(false);
            this.user_group_name.setTextInputAllowed(false);
            
            this.company_name.setItemCaptionPropertyId(this.NAME_COLUMN_IDENTIFIER);
            this.user_group_name.setItemCaptionPropertyId(this.NAME_COLUMN_IDENTIFIER);
            
            this.first_name_label.setVisible(false);
            this.last_name_label.setVisible(false);
            this.email_label.setVisible(false);
            this.username_label.setVisible(false);
            this.password_label.setVisible(false);
            this.confirm_password_label.setVisible(false);
            this.company_label.setVisible(false);
            this.user_group_label.setVisible(false);
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout createUserLayout = new GridLayout(2,10);
            createUserLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            //Fields and components for the  layout.
            Label createUserTitle = new Label("Edit user");
            
            Button back = new Button("Back");
            back.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    company_container.removeAllItems();
                    user_group_container.removeAllItems();
                    nav.navigateTo(manageUsersView);
                }
            });
            
            Button editUser = new Button("Done");
            editUser.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){
                    administration.user.settings.clear_error_messages();
                    
                    first_name_label.setVisible(false);
                    last_name_label.setVisible(false);
                    email_label.setVisible(false);
                    username_label.setVisible(false);
                    password_label.setVisible(false);
                    confirm_password_label.setVisible(false);
                    company_label.setVisible(false);
                    user_group_label.setVisible(false);
                    
                    if (userPwordField.getValue().equals(userCPwordField.getValue())) {
                        if (administration.user.edit(
                                selectedUser,
                                userFnameField.getValue(),
                                userLnameField.getValue(),
                                userEmailField.getValue(),
                                userUnameField.getValue(),
                                userPwordField.getValue(),
                                company_name.getValue().toString(),
                                user_group_name.getValue().toString())) {
                            System.out.println("User updated.");
                            Notification.show("\tUser updated\t",
                            Notification.TYPE_HUMANIZED_MESSAGE);
                        } else {
                            System.out.println("User not updated.");
                            
                            if (administration.user.settings.FIRST_NAME_ERROR_MESSAGE != null) {
                                first_name_label.setValue(administration.user.settings.FIRST_NAME_ERROR_MESSAGE);
                                if (!first_name_label.isVisible()) first_name_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.LAST_NAME_ERROR_MESSAGE != null) {
                                last_name_label.setValue(administration.user.settings.LAST_NAME_ERROR_MESSAGE);
                                if (!last_name_label.isVisible()) last_name_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.EMAIL_ERROR_MESSAGE != null) {
                                email_label.setValue(administration.user.settings.EMAIL_ERROR_MESSAGE);
                                if (!email_label.isVisible()) email_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.USERNAME_ERROR_MESSAGE != null) {
                                username_label.setValue(administration.user.settings.USERNAME_ERROR_MESSAGE);
                                if (!username_label.isVisible()) username_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.PASSWORD_ERROR_MESSAGE != null) {
                                password_label.setValue(administration.user.settings.PASSWORD_ERROR_MESSAGE);
                                if (!password_label.isVisible()) password_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.COMPANY_ERROR_MESSAGE != null) {
                                company_label.setValue(administration.user.settings.COMPANY_ERROR_MESSAGE);
                                if (!company_label.isVisible()) company_label.setVisible(true);
                            }
                            
                            if (administration.user.settings.USER_GROUP_ERROR_MESSAGE != null) {
                                user_group_label.setValue(administration.user.settings.USER_GROUP_ERROR_MESSAGE);
                                if (!user_group_label.isVisible()) user_group_label.setVisible(true);
                            }
                        }
                    } else {
                        confirm_password_label.setValue("The provided passwords do not match.");
                        if (!confirm_password_label.isVisible()) confirm_password_label.setVisible(true);
                    }
                }
            });
            editUser.setStyleName("titel_padding");
            
            GridLayout bLayout = new GridLayout(2,1);
            bLayout.setStyleName("top_padding");
            bLayout.addComponent(editUser,0,0);
            bLayout.setComponentAlignment(editUser, Alignment.MIDDLE_LEFT);
            bLayout.addComponent(back,1,0);
            bLayout.setComponentAlignment(back, Alignment.MIDDLE_RIGHT);
            bLayout.setWidth("100%");
            bLayout.setHeight("100%");
            
            //Adding the fields to the layout.
            createUserLayout.addComponent(createUserTitle, 0, 0);
            createUserLayout.addComponent(this.userFnameField, 0, 1);
            createUserLayout.addComponent(this.userLnameField, 0, 2);
            createUserLayout.addComponent(this.userEmailField, 0, 3);
            createUserLayout.addComponent(this.userUnameField, 0, 4);
            createUserLayout.addComponent(this.userPwordField, 0, 5);
            createUserLayout.addComponent(this.userCPwordField, 0, 6);
            createUserLayout.addComponent(this.company_name, 0, 7);
            createUserLayout.addComponent(this.user_group_name, 0, 8);
            
            createUserLayout.addComponent(bLayout, 0, 9);
            
            createUserLayout.addComponent(this.user_group_label, 1, 8);
            createUserLayout.setComponentAlignment(this.user_group_label, Alignment.MIDDLE_LEFT);
            createUserLayout.addComponent(this.company_label, 1, 7);
            createUserLayout.setComponentAlignment(this.company_label, Alignment.MIDDLE_LEFT);
            createUserLayout.addComponent(this.confirm_password_label, 1, 6);
            createUserLayout.setComponentAlignment(this.confirm_password_label, Alignment.MIDDLE_LEFT);
            createUserLayout.addComponent(this.password_label, 1, 5);
            createUserLayout.setComponentAlignment(this.password_label, Alignment.MIDDLE_LEFT);
            createUserLayout.addComponent(this.username_label, 1, 4);
            createUserLayout.setComponentAlignment(this.username_label, Alignment.MIDDLE_LEFT);
            createUserLayout.addComponent(this.email_label, 1, 3);
            createUserLayout.setComponentAlignment(this.email_label, Alignment.MIDDLE_LEFT);
            createUserLayout.addComponent(this.last_name_label, 1, 2);
            createUserLayout.setComponentAlignment(this.last_name_label, Alignment.MIDDLE_LEFT);
            createUserLayout.addComponent(this.first_name_label, 1, 1);
            createUserLayout.setComponentAlignment(this.first_name_label, Alignment.MIDDLE_LEFT);

            mainlayout.addComponent(createUserLayout);
            mainlayout.setComponentAlignment(createUserLayout, Alignment.MIDDLE_CENTER);
        }
        
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_users && selectedUser != null) {
                    company_name.removeAllItems();
                    user_group_name.removeAllItems();
                    
                    administration = new Administration(user.user_group);
                    
                    for (CompanyRow company : administration.object_collections.companies()) {
                        Item newItem = company_container.addItem(company.id);
                        newItem.getItemProperty(this.NAME_COLUMN_IDENTIFIER).setValue(company.name);
                    }
                    
                    for (UserGroups group : administration.object_collections.user_groups()) {
                        Item newItem = user_group_container.addItem(group.id);
                        newItem.getItemProperty(this.NAME_COLUMN_IDENTIFIER).setValue(group.name);
                    }
                    
                    company_name.setValue(selectedUser.company.id);
                    user_group_name.setValue(selectedUser.user_group.id);
                    
                    userFnameField.setValue(selectedUser.first_name);
                    userLnameField.setValue(selectedUser.last_name);
                    userEmailField.setValue(selectedUser.email);
                    userUnameField.setValue(selectedUser.username);
                } else {
                    nav.navigateTo(manageUsersView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //EditUserLayout end
    
    //ManageCompanyLayout start
    public class ManageCompanyLayout extends GridLayout implements View{
        public Administration administration = new Administration();
        
        public final String HIDDEN_COLUMN_IDENTIFIER = "id";
        public final String COMPANY_NAME_COLUMN_IDENTIFIER = "Name";
        public final String WEBSITE_COLUMN_IDENTIFIER = "Website";
        public final String DETAILS_COLUMN_IDENTIFIER = "Details";
        
        public final IndexedContainer tableContainer = new IndexedContainer();
        public final Grid companyTable = new Grid(tableContainer);
        
        public ManageCompanyLayout(){
            
            this.tableContainer.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.COMPANY_NAME_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.WEBSITE_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.DETAILS_COLUMN_IDENTIFIER, String.class, null);
            
            this.companyTable.getColumn(this.HIDDEN_COLUMN_IDENTIFIER).setHidden(true);
            this.companyTable.setSelectionMode(SelectionMode.SINGLE);
            
            setWidth("100%");
            setHeight("100%");

            final GridLayout companylayout = new GridLayout(1,4);
            companylayout.setWidth("90%");
            companylayout.setHeight("90%");
            addComponent(companylayout);
            setComponentAlignment(companylayout, Alignment.MIDDLE_CENTER);

            Label testLbl = new Label("Companys");
            companylayout.addComponent(testLbl,0,0);
            companylayout.addComponent(companyTable, 0, 1);
            companylayout.setColumnExpandRatio(0, 1);
            companylayout.setRowExpandRatio(1,1);
            companyTable.setSizeFull();
            
            //Layout for the buttons
            HorizontalLayout buttonLayout = new HorizontalLayout();
            
            Button back = new Button("Back");
            back.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    tableContainer.removeAllItems();
                    nav.navigateTo(logsView);
                }
            });
            
            Button createCompany = new Button("Create company");
            createCompany.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    tableContainer.removeAllItems();
                    nav.navigateTo(createCompanyView);
                }
            });

            Button editCompanyButton = new Button("Edit Company");
            editCompanyButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) companyTable.getSelectionModel()).getSelectedRow();

//                    if (selected != null) {
//                        selectedUser = new CurrentUser(companyTable.getContainerDataSource().getItem(selected).getItemProperty(HIDDEN_COLUMN_IDENTIFIER).getValue().toString());
//                        System.out.println("Selected company id: " + selectedUser.id);
//                        tableContainer.removeAllItems();
//                        nav.navigateTo(editUserView);
//                    } else {
//                        System.out.println("Nothing selected.");
//                    }
                }
            });
            
            //Adding the buttons to the layout
            buttonLayout.addComponent(createCompany);
            buttonLayout.addComponent(editCompanyButton);
            buttonLayout.addComponent(back);
            companylayout.addComponent(buttonLayout, 0, 2);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_users) {

                    administration = new Administration(user.user_group);
                    
                    for (CompanyRow companyRow : administration.object_collections.companies()) {
                        Item userItem = tableContainer.addItem(companyRow.id);
                        userItem.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(companyRow.id);
                        userItem.getItemProperty(this.COMPANY_NAME_COLUMN_IDENTIFIER).setValue(companyRow.name);
                        userItem.getItemProperty(this.WEBSITE_COLUMN_IDENTIFIER).setValue(companyRow.website);
                        userItem.getItemProperty(this.DETAILS_COLUMN_IDENTIFIER).setValue(companyRow.details);
                    }
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //ManageCompanyLayout end
    
    //CreateCompanyLayout start
    public class CreateCompanyLayout extends GridLayout implements View {
    
        public TextField companyNameField = new TextField("Company name");
        public TextField companyWebsiteField = new TextField("Website");
        public TextField companyDetailsField = new TextField("Details");
        
        public CreateCompanyLayout(){
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout createCompanyLayout = new GridLayout(1,5);
            createCompanyLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            // components for the layout
            Label createCompanyTitel = new Label("Create a new company");
            
            Button backButton = new Button("Back");
            backButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    nav.navigateTo(logsView);
                }
            });
            
            Button createCompanyButton = new Button("Create");
            createCompanyButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if(companyNameField.getValue() != null){
                        
                    }
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            });
            
            //creating a layout for the buttons
            HorizontalLayout bLayout = new HorizontalLayout();
            bLayout.setStyleName("top_padding");
            bLayout.setWidth("100%");
            bLayout.setHeight("100%");
            
            //adding the buttons to the buttonlayout
            bLayout.addComponent(createCompanyButton);
            bLayout.addComponent(backButton);
            bLayout.setComponentAlignment(createCompanyButton, Alignment.MIDDLE_LEFT);
            bLayout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT);
            
            //adding the componets to the grid
            createCompanyLayout.addComponent(createCompanyTitel,0,0);
            createCompanyLayout.addComponent(companyNameField,0,1);
            createCompanyLayout.addComponent(companyWebsiteField,0,2);
            createCompanyLayout.addComponent(companyDetailsField,0,3);
            createCompanyLayout.addComponent(bLayout,0,4);
            
            //adding the createCompanyLayout to the mainLayout
            mainlayout.addComponent(createCompanyLayout);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            throw new UnsupportedOperationException(); //To change body of generated methods, choose Tools | Templates.
        }
    }
    //CreateCompanyLayout ends
    
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
