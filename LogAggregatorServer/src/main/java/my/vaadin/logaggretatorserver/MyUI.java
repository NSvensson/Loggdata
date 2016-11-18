package my.vaadin.logaggretatorserver;

import com.vaadin.annotations.PreserveOnRefresh;
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
import com.vaadin.shared.ui.grid.ScrollDestination;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;


/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@PreserveOnRefresh
public class MyUI extends UI {
    private final String loginView = "";
    private final String logsView = "Logs";
    private final String createUserView = "CreateUser";
    private final String editUserView = "EditUser";
    private final String createCompanyView = "CreateCompany";
    private final String manageUsersView = "ManageUsers";
    private final String manageCompaniesView = "ManageCompanies";
    private final String editCompanyView = "EditCompanies";
    private final String manageApplicationView = "ManageApplications";
    private final String createApplicationView = "CreateApplications";
    private final String editApplicationView = "EditApplications";
    private final String manageUserGroupsView = "ManageUserGroups";
    private final String createUserGroupsView = "CreateUserGroups";
    private final String editUserGroupsView = "EditUserGroups";
    
    private CurrentUser user;
    
    private CurrentUser selectedUser = null;
    private CompanyRow selectedCompany = null;
    private ApplicationRow selectedApplication = null;
    private UserGroups selectedUserGroup = null;
    
    private Navigator nav = new Navigator(this, this);
    private View current_view;
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Configurations.readPropertiesFile();
        
        getPage().setTitle("Log server");
        
        nav.addView(loginView, (current_view = new LoginLayout()));
        nav.addView(logsView, new ViewLogsLayout());
        nav.addView(createUserView, new CreateUserLayout());
        nav.addView(createCompanyView, new CreateCompanyLayout());
        nav.addView(manageUsersView, new ManageUsersLayout());
        nav.addView(editUserView, new EditUserLayout());
        nav.addView(manageCompaniesView, new ManageCompanyLayout());
        nav.addView(editCompanyView, new EditCompanyLayout());
        nav.addView(manageApplicationView, new ManageApplicationLayout());
        nav.addView(createApplicationView, new CreateApplicationLayout());
        nav.addView(editApplicationView, new EditApplicationLayout());
        nav.addView(manageUserGroupsView, new ManageUserGroupLayout());
        nav.addView(createUserGroupsView, new CreateUserGroupLayout());
        nav.addView(editUserGroupsView, new EditUserGroupLayout());
    }
    
    //LoginLayout start
    public class LoginLayout extends GridLayout implements View {
        
        public final TextField usernameTextField = new TextField("Username");
        public final PasswordField passwordTextField = new PasswordField("Password");
        
        public LoginLayout() {
            final GridLayout layout = new GridLayout(1,4);
            setWidth("100%");
            setHeight("100%");
            layout.addStyleName("login-grid-layout");
            layout.setSpacing(true);

            addComponent(layout);
            setComponentAlignment(layout, Alignment.MIDDLE_CENTER);

            Label startTitelLogin = new Label("Login");
            layout.addComponent(startTitelLogin, 0, 0);
            layout.setComponentAlignment(startTitelLogin, Alignment.MIDDLE_CENTER);

            layout.addComponent(usernameTextField,0,1);
            layout.setComponentAlignment(usernameTextField, Alignment.TOP_LEFT);

            layout.addComponent(passwordTextField,0,2);
            layout.setComponentAlignment(passwordTextField, Alignment.TOP_LEFT);

            Button loginbtn = new Button("Login");
            loginbtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    user = new CurrentUser(
                            usernameTextField.getValue(),
                            DataManaging.hashString(passwordTextField.getValue()));
                    
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
            layout.addComponent(loginbtn, 0, 3);
        }
        
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            usernameTextField.clear();
            passwordTextField.clear();
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
        public final Button manage_applications = new Button("Manage applications");
        public final Button manage_usergroups = new Button("Manage user groups");
        
        public final Window subWindow = new Window("Event information");
        
        public ViewLogsLayout() {
            
            this.tableContainer.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.APPLICATION_NAME_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.DATE_COLUMN_NAME_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.EVENT_COLUMN_NAME_IDENTIFIER, String.class, null);
            
            this.logtable.getColumn(this.HIDDEN_COLUMN_IDENTIFIER).setHidden(true);
            this.logtable.getColumn(this.APPLICATION_NAME_COLUMN_IDENTIFIER).setWidth(200);
            this.logtable.getColumn(this.DATE_COLUMN_NAME_IDENTIFIER).setWidth(200);
            this.logtable.getColumn(this.EVENT_COLUMN_NAME_IDENTIFIER).setWidthUndefined();
            this.logtable.getColumn(this.EVENT_COLUMN_NAME_IDENTIFIER).setResizable(false);
            
            setWidth("100%");
            setHeight("100%");

            final GridLayout loglayout = new GridLayout(1,4);
            loglayout.setWidth("90%");
            loglayout.setHeight("90%");
            addComponent(loglayout);
            setComponentAlignment(loglayout, Alignment.MIDDLE_CENTER);

            final GridLayout gTitleLayout = new GridLayout(3,1);
            gTitleLayout.setStyleName("titel_padding");
            Label testLbl = new Label("Your log.");
            gTitleLayout.addComponent(testLbl,0,0);

            this.logtable.setSizeFull();
            logtable.addItemClickListener(event -> {
                logtable.scrollTo(event.getItemId(), ScrollDestination.START);
                subWindow.close();
                LogItem log = new LogItem(event.getItem());
                
                subWindow.setHeight("800");
                subWindow.setWidth("1200");
                //Grid layout to add the componets to
                GridLayout subInformationLayout = new GridLayout(1,3);
                subInformationLayout.setMargin(true);
                subInformationLayout.setSizeFull();
                subWindow.setContent(subInformationLayout);
                
                HorizontalLayout titelLayout = new HorizontalLayout();
                Label subWindowApplicationLabel = new Label("Application: "+ log.application_name);
                subWindowApplicationLabel.setStyleName("title_padding");
                Label subWindowLogdateLabel = new Label("Log date: "+log.log_date);
                Label subWindowLogEventLabel = new Label(log.log_event);
                
                Panel logPanel = new Panel("Event");
                logPanel.setSizeFull();
                logPanel.setContent(subWindowLogEventLabel);
                
                titelLayout.addComponent(subWindowApplicationLabel);
                titelLayout.addComponent(subWindowLogdateLabel);
                subInformationLayout.addComponent(titelLayout, 0, 0);
                subInformationLayout.addComponent(logPanel, 0, 1);
                subInformationLayout.setRowExpandRatio(1, 1);
                
                Button closeButton = new Button("Close");
                closeButton.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        subWindow.close();
                    }
                });
                subInformationLayout.addComponent(closeButton,0,2);
                subInformationLayout.setComponentAlignment(closeButton, Alignment.BOTTOM_RIGHT);
                
                subWindow.center();
                addWindow(subWindow);
                
            });
            
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
            
            Button refresh = new Button("Refresh");
            refresh.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    refresh_logs_view();
                }
            });
            
            buttonLayout.addComponent(refresh);
            buttonLayout.addComponent(out);
            buttonLayout.setComponentAlignment(out, Alignment.TOP_RIGHT);
            
            loglayout.addComponent(buttonLayout,0,2);
            loglayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);
            
            HorizontalLayout administrationLayout = new HorizontalLayout();
            
            manage_users.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (user.user_group.manage_users) {
                        tableContainer.removeAllItems();
                        comboBoxContainer.removeAllItems();
                        nav.navigateTo(manageUsersView);
                    }
                }
            });
            
            manage_companies.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (user.user_group.manage_companies) {
                        tableContainer.removeAllItems();
                        comboBoxContainer.removeAllItems();
                        nav.navigateTo(manageCompaniesView);
                    }
                }
            });
            
            manage_applications.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (user.user_group.manage_applications) {
                        tableContainer.removeAllItems();
                        comboBoxContainer.removeAllItems();
                        nav.navigateTo(manageApplicationView);
                    }
                }
            });
            
            manage_usergroups.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (user.user_group.manage_groups) {
                        tableContainer.removeAllItems();
                        comboBoxContainer.removeAllItems();
                        nav.navigateTo(manageUserGroupsView);
                    }
                }
            });
            
            manage_users.setEnabled(false);
            manage_users.setVisible(false);
            manage_companies.setEnabled(false);
            manage_companies.setVisible(false);
            manage_applications.setEnabled(false);
            manage_applications.setVisible(false);
            manage_usergroups.setEnabled(false);
            manage_usergroups.setVisible(false);
            
            administrationLayout.addComponent(manage_users);
            administrationLayout.addComponent(manage_companies);
            administrationLayout.addComponent(manage_applications);
            administrationLayout.addComponent(manage_usergroups);
            loglayout.addComponent(administrationLayout,0,3);
            
            loglayout.setRowExpandRatio(1,1);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            manage_users.setEnabled(false);
            manage_users.setVisible(false);
            manage_companies.setEnabled(false);
            manage_companies.setVisible(false);
            manage_applications.setEnabled(false);
            manage_applications.setVisible(false);
            manage_usergroups.setEnabled(false);
            manage_usergroups.setVisible(false);
            
            logtable.deselectAll();
            tableContainer.removeAllItems();
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
                                Item newLog = tableContainer.addItem(log.id);
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
                if (user.user_group.manage_applications) {
                    if (!manage_applications.isVisible()) manage_applications.setVisible(true);
                    if (!manage_applications.isEnabled()) manage_applications.setEnabled(true);
                }
                if (user.user_group.manage_groups) {
                    if (!manage_usergroups.isVisible()) manage_usergroups.setVisible(true);
                    if (!manage_usergroups.isEnabled()) manage_usergroups.setEnabled(true);
                }
                
                logtable.setCellStyleGenerator(rowRef -> {
                        if (this.EVENT_COLUMN_NAME_IDENTIFIER.equals(rowRef.getPropertyId())) {
                            String logEvent = rowRef.getItem()
                                                    .getItemProperty(this.EVENT_COLUMN_NAME_IDENTIFIER)
                                                    .getValue()
                                                    .toString()
                                                    .toLowerCase();
                            
                            if (logEvent.contains("error")) return "red";
                            else if (logEvent.contains("failure") || logEvent.contains("failed")) return "yellow";
                            else if (logEvent.contains("exception")) return "orange";
                            else return null;
                        } else return null;
                });
            } else {
                nav.navigateTo(loginView);
            }
        }
        
        public class LogItem {
        
            public final String application_name;
            public final String log_date;
            public final String log_event;
            
            public LogItem(Item item) {
                
                this.application_name = item.getItemProperty(APPLICATION_NAME_COLUMN_IDENTIFIER).getValue().toString();
                this.log_date = item.getItemProperty(DATE_COLUMN_NAME_IDENTIFIER).getValue().toString();
                this.log_event = item.getItemProperty(EVENT_COLUMN_NAME_IDENTIFIER).getValue().toString();
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
        
        public final Window subConfirmWindow = new Window("Sub-window");
        
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
                    CompanyRow[] companies;
                    if ((companies = administration.object_collections.companies()) != null &&
                         companies.length >= 1 &&
                         companies[0] != null) {
                        
                        tableContainer.removeAllItems();
                        nav.navigateTo(createUserView);
                    } else {
                        Notification.show("No user company found in the database.\n" +
                                          "Please create a company before attempting to create a user.", Notification.Type.WARNING_MESSAGE);
                    }
                }
            });

            Button editUserButton = new Button("Edit user");
            editUserButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) usertable.getSelectionModel()).getSelectedRow();

                    if (selected != null) {
                        selectedUser = new CurrentUser(selected.toString());
                        System.out.println("Selected user id: " + selectedUser.id);
                        tableContainer.removeAllItems();
                        nav.navigateTo(editUserView);
                    } else {
                        System.out.println("Nothing selected.");
                    }
                }
            });
            
            
            Button deleteUserButton = new Button("Delete user");
            deleteUserButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) usertable.getSelectionModel()).getSelectedRow();
                    
                    if (selected != null) {
                        selectedUser = new CurrentUser(selected.toString());
                        subConfirmWindow.setHeight(250,Unit.PIXELS);
                        subConfirmWindow.setWidth(350,Unit.PIXELS);
                        subConfirmWindow.setResizable(false);

                        GridLayout subConfirmLayout = new GridLayout(1,3);
                        subConfirmLayout.setMargin(true);
                        subConfirmLayout.setSizeFull();
                        subConfirmWindow.setContent(subConfirmLayout);

                        // Put some components in it
                        Label titelConfirmWindow = new Label("Delete user");
                        subConfirmLayout.addComponent(titelConfirmWindow, 0, 0);
                        Label contentConfirmWindow = new Label("You are about to delete "+selectedUser.username+"!\nAre you sure?");
                        subConfirmLayout.addComponent(contentConfirmWindow, 0, 1);
                        
                        Button confirmButton = new Button("Yes");
                        confirmButton.addClickListener(new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                if (administration.user.remove(selectedUser)) {
                                    tableContainer.removeItem(selected);
                                    subConfirmWindow.close();
                                }
                            }
                        });
                                
                        Button declineButton = new Button("No");
                        declineButton.addClickListener(new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                subConfirmWindow.close();
                            }
                        });
                        
                        //Adding buttons to the window
                        HorizontalLayout bLayout = new HorizontalLayout();
                        bLayout.addComponent(confirmButton);
                        bLayout.addComponent(declineButton);
                        
                        subConfirmLayout.addComponent(bLayout, 0, 2);
                        // Center it in the browser window
                        subConfirmWindow.center();

                        // Open it in the UI
                        addWindow(subConfirmWindow);
                    
                            //If user removed
                    } else {
                            //If user not removed
                    }
                }
            });
            
            buttonLayout.addComponent(createUser);
            buttonLayout.addComponent(editUserButton);
            buttonLayout.addComponent(deleteUserButton);
            buttonLayout.addComponent(back);
            userlayout.addComponent(buttonLayout, 0, 2);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            usertable.deselectAll();
            tableContainer.removeAllItems();
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_users) {

                    administration = new Administration(user.user_group, user.company);
                    
                    CurrentUser[] found_users;
                    if ((found_users = administration.object_collections.users()) != null) {
                        for (CurrentUser userRow : found_users) {
                            Item userItem = tableContainer.addItem(userRow.id);
                            userItem.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(userRow.id);
                            userItem.getItemProperty(this.FIRST_NAME_COLUMN_IDENTIFIER).setValue(userRow.first_name);
                            userItem.getItemProperty(this.LAST_NAME_NAME_COLUMN_IDENTIFIER).setValue(userRow.last_name);
                            userItem.getItemProperty(this.EMAIL_COLUMN_IDENTIFIER).setValue(userRow.email);
                            userItem.getItemProperty(this.USERNAME_COLUMN_IDENTIFIER).setValue(userRow.username);
                            userItem.getItemProperty(this.USER_COMPANY_COLUMN_IDENTIFIER).setValue(userRow.company.name);
                            userItem.getItemProperty(this.USER_GROUP_COLUMN_IDENTIFIER).setValue(userRow.user_group.name);
                        }
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
            GridLayout createUserLayout = new GridLayout(2,10);
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
                            Notification.show("\tUser created\t",
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
            
            createUserLayout.addComponent(this.user_group_label, 1, 8);
            createUserLayout.setComponentAlignment(this.user_group_label, Alignment.BOTTOM_LEFT);
            createUserLayout.addComponent(this.company_label, 1, 7);
            createUserLayout.setComponentAlignment(this.company_label, Alignment.BOTTOM_LEFT);
            createUserLayout.addComponent(this.confirm_password_label, 1, 6);
            createUserLayout.setComponentAlignment(this.confirm_password_label, Alignment.BOTTOM_LEFT);
            createUserLayout.addComponent(this.password_label, 1, 5);
            createUserLayout.setComponentAlignment(this.password_label, Alignment.BOTTOM_LEFT);
            createUserLayout.addComponent(this.username_label, 1, 4);
            createUserLayout.setComponentAlignment(this.username_label, Alignment.BOTTOM_LEFT);
            createUserLayout.addComponent(this.email_label, 1, 3);
            createUserLayout.setComponentAlignment(this.email_label, Alignment.BOTTOM_LEFT);
            createUserLayout.addComponent(this.last_name_label, 1, 2);
            createUserLayout.setComponentAlignment(this.last_name_label, Alignment.BOTTOM_LEFT);
            createUserLayout.addComponent(this.first_name_label, 1, 1);
            createUserLayout.setComponentAlignment(this.first_name_label, Alignment.BOTTOM_LEFT);
            
            mainlayout.addComponent(createUserLayout);
            mainlayout.setComponentAlignment(createUserLayout, Alignment.MIDDLE_RIGHT);
        }
        
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            administration.user.settings.clear_error_messages();
            
            first_name_label.setVisible(false);
            last_name_label.setVisible(false);
            email_label.setVisible(false);
            username_label.setVisible(false);
            password_label.setVisible(false);
            confirm_password_label.setVisible(false);
            company_label.setVisible(false);
            user_group_label.setVisible(false);
            
            userFnameField.clear();
            userLnameField.clear();
            userEmailField.clear();
            userUnameField.clear();
            userPwordField.clear();
            userCPwordField.clear();
            
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_users) {
                    company_name.removeAllItems();
                    user_group_name.removeAllItems();
                    
                    administration = new Administration(user.user_group, user.company);
                    
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
                    userFnameField.clear();
                    userLnameField.clear();
                    userEmailField.clear();
                    userUnameField.clear();
                    userPwordField.clear();
                    userCPwordField.clear();
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
            current_view = this;
            
            userFnameField.clear();
            userLnameField.clear();
            userEmailField.clear();
            userUnameField.clear();
            userPwordField.clear();
            userCPwordField.clear();
            
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_users && selectedUser != null) {
                    company_name.removeAllItems();
                    user_group_name.removeAllItems();
                    
                    administration = new Administration(user.user_group, user.company);
                    
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
        
        public final Window subConfirmWindow = new Window();
        
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

                    if (selected != null) {
                        selectedCompany = new CompanyRow(selected.toString());
                        System.out.println("Selected company id: " + selectedCompany.id);
                        tableContainer.removeAllItems();
                        nav.navigateTo(editCompanyView);
                    } else {
                        System.out.println("Nothing selected.");
                    }
                }
            });
            
            Button deleteCompanyButton = new Button("Delete company");
            deleteCompanyButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) companyTable.getSelectionModel()).getSelectedRow();
                    
                    if (selected != null) {
                        selectedCompany = new CompanyRow(selected.toString());
                        subConfirmWindow.setHeight(250,Unit.PIXELS);
                        subConfirmWindow.setWidth(350,Unit.PIXELS);
                        subConfirmWindow.setResizable(false);

                        GridLayout subConfirmLayout = new GridLayout(1,4);
                        subConfirmLayout.setMargin(true);
                        subConfirmLayout.setSizeFull();
                        subConfirmWindow.setContent(subConfirmLayout);

                        // Put some components in it
                        Label titelConfirmWindow = new Label("Delete company");
                        subConfirmLayout.addComponent(titelConfirmWindow, 0, 0);
                        Label contentConfirmWindow = new Label("You are about to delete "+selectedCompany.name+"!\nAre you sure?");
                        subConfirmLayout.addComponent(contentConfirmWindow, 0, 1);
                        
                        Button confirmButton = new Button("Yes");
                        confirmButton.addClickListener(new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                
                                GridLayout subExtraConfirmLayout = new GridLayout(1,3);
                                subExtraConfirmLayout.setMargin(true);
                                subExtraConfirmLayout.setSizeFull();
                                subConfirmWindow.setContent(subExtraConfirmLayout);
                                
                                Label ConfirmWindowLabel = new Label("This will delete all users ,\n"
                                                                    +"applications and logs bound to "+selectedCompany.name);
                                subExtraConfirmLayout.addComponent(ConfirmWindowLabel, 0, 0);
                                Label contentConfirmWindow = new Label("\nAre you sure?");
                                subExtraConfirmLayout.addComponent(contentConfirmWindow, 0, 1);
                                
                                Button confirmExtra = new Button("Yes");
                                confirmExtra.addClickListener(new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(Button.ClickEvent event) {
                                        if (administration.company.remove(selectedCompany.id)) {
                                            tableContainer.removeItem(selected);
                                            subConfirmWindow.close();
                                        } else {
                                        }
                                    }
                                });
                                
                                Button declineButton = new Button("No");
                                declineButton.addClickListener(new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(Button.ClickEvent event) {
                                        subConfirmWindow.close();
                                    }
                                });
                                HorizontalLayout bLayout = new HorizontalLayout();
                                bLayout.addComponent(confirmExtra);
                                bLayout.addComponent(declineButton);
                        
                                 subExtraConfirmLayout.addComponent(bLayout, 0, 2);
                                // Center it in the browser window
                                subConfirmWindow.center();

                                // Open it in the UI
                                
//                                addWindow(subConfirmWindow);
//                                
                            }
                        });
                                
                        Button declineButton = new Button("No");
                        declineButton.addClickListener(new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                subConfirmWindow.close();
                            }
                        });
                        
                        //Adding buttons to the window
                        HorizontalLayout bLayout = new HorizontalLayout();
                        bLayout.addComponent(confirmButton);
                        bLayout.addComponent(declineButton);
                        
                        subConfirmLayout.addComponent(bLayout, 0, 2);
                        // Center it in the browser window
                        subConfirmWindow.center();

                        // Open it in the UI
                        addWindow(subConfirmWindow);
                    
                            //If user removed
                    } else {
                            //If user not removed
                    }
                }
            });
            
            //Adding the buttons to the layout
            buttonLayout.addComponent(createCompany);
            buttonLayout.addComponent(editCompanyButton);
            buttonLayout.addComponent(deleteCompanyButton);
            buttonLayout.addComponent(back);
            companylayout.addComponent(buttonLayout, 0, 2);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            companyTable.deselectAll();
            tableContainer.removeAllItems();
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_companies) {
                    
                    administration = new Administration(user.user_group, user.company);
                    
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
        
        public Administration administration = new Administration();
        
        public TextField companyNameField = new TextField("Company name");
        public TextField companyWebsiteField = new TextField("Website");
        public TextField companyDetailsField = new TextField("Details");
        
        public Label company_Name_Label = new Label();
        public Label company_Website_Label = new Label();
        public Label company_Details_Label = new Label();
        
        public CreateCompanyLayout() {
            
            company_Name_Label.setVisible(false);
            company_Website_Label.setVisible(false);
            company_Details_Label.setVisible(false);
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout createCompanyLayout = new GridLayout(2,5);
            createCompanyLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            // components for the layout
            Label createCompanyTitel = new Label("Create a new company");
            
            Button backButton = new Button("Back");
            backButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    nav.navigateTo(manageCompaniesView);
                }
            });
            
            Button createCompanyButton = new Button("Create");
            createCompanyButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    administration.company.settings.clear_error_messages();
                    
                    company_Name_Label.setVisible(false);
                    company_Website_Label.setVisible(false);
                    company_Details_Label.setVisible(false);
                    
                    if (administration.company.create(
                            companyNameField.getValue(),
                            companyWebsiteField.getValue(),
                            companyDetailsField.getValue())){
                        System.out.println("Company created.");
                        Notification.show("\tCompany created\t", Notification.TYPE_HUMANIZED_MESSAGE);
                    } else {
                        System.out.println("company not created.");

                        if (administration.company.settings.NAME_ERROR_MESSAGE != null) {
                            company_Name_Label.setValue(administration.company.settings.NAME_ERROR_MESSAGE);
                            if (!company_Name_Label.isVisible()) company_Name_Label.setVisible(true);
                        }

                        if (administration.company.settings.WEBSITE_ERROR_MESSAGE != null) {
                            company_Website_Label.setValue(administration.company.settings.WEBSITE_ERROR_MESSAGE);
                            if (!company_Website_Label.isVisible()) company_Website_Label.setVisible(true);
                        }

                        if (administration.company.settings.DETAILS_ERROR_MESSAGE != null) {
                            company_Details_Label.setValue(administration.company.settings.DETAILS_ERROR_MESSAGE);
                            if (!company_Details_Label.isVisible()) company_Details_Label.setVisible(true);
                        }
                    }
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
            createCompanyLayout.addComponent(company_Name_Label,1,1);
            createCompanyLayout.addComponent(companyWebsiteField,0,2);
            createCompanyLayout.addComponent(company_Website_Label,1,2);
            createCompanyLayout.addComponent(companyDetailsField,0,3);
            createCompanyLayout.addComponent(company_Details_Label,1,3);
            createCompanyLayout.addComponent(bLayout,0,4);
            
            //adding the createCompanyLayout to the mainLayout
            mainlayout.addComponent(createCompanyLayout);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;

            companyNameField.clear();
            companyWebsiteField.clear();
            companyDetailsField.clear();
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_companies) {
                    administration = new Administration(user.user_group, user.company);
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //CreateCompanyLayout ends
    
    //EditCompaniesLayout start
    public class EditCompanyLayout extends GridLayout implements View {
        
        public TextField companyNameField = new TextField("Company name");
        public TextField companyWebsiteField = new TextField("Website");
        public TextField companyDetailsField = new TextField("Details");
        
        public Label company_Name_Label = new Label();
        public Label company_Website_Label = new Label();
        public Label company_Details_Label = new Label();
        
        public Administration administration = new Administration();
        
        public EditCompanyLayout() {
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout editCompanyLayout = new GridLayout(1,5);
            editCompanyLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            // components for the layout
            Label createCompanyTitel = new Label("Edit company");
            
            Button backButton = new Button("Back");
            backButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){
                    companyNameField.clear();
                    companyWebsiteField.clear();
                    companyDetailsField.clear();
                    selectedCompany = null;
                    nav.navigateTo(manageCompaniesView);
                }
            });
            
            Button createCompanyButton = new Button("Done");
            createCompanyButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    administration.company.settings.clear_error_messages();
                    
                    company_Name_Label.setVisible(false);
                    company_Website_Label.setVisible(false);
                    company_Details_Label.setVisible(false);
                    
                    if (administration.company.edit(
                            selectedCompany,
                            companyNameField.getValue(),
                            companyWebsiteField.getValue(),
                            companyDetailsField.getValue())){
                        System.out.println("Company changed.");
                        Notification.show("\tCompany changed\t", Notification.TYPE_HUMANIZED_MESSAGE);
                    } else {
                        System.out.println("company not changed.");

                        if (administration.company.settings.NAME_ERROR_MESSAGE != null) {
                            company_Name_Label.setValue(administration.company.settings.NAME_ERROR_MESSAGE);
                            if (!company_Name_Label.isVisible()) company_Name_Label.setVisible(true);
                        }

                        if (administration.company.settings.WEBSITE_ERROR_MESSAGE != null) {
                            company_Website_Label.setValue(administration.company.settings.WEBSITE_ERROR_MESSAGE);
                            if (!company_Website_Label.isVisible()) company_Website_Label.setVisible(true);
                        }

                        if (administration.company.settings.DETAILS_ERROR_MESSAGE != null) {
                            company_Details_Label.setValue(administration.company.settings.DETAILS_ERROR_MESSAGE);
                            if (!company_Details_Label.isVisible()) company_Details_Label.setVisible(true);
                        }
                    }
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
            editCompanyLayout.addComponent(createCompanyTitel,0,0);
            editCompanyLayout.addComponent(companyNameField,0,1);
            editCompanyLayout.addComponent(companyWebsiteField,0,2);
            editCompanyLayout.addComponent(companyDetailsField,0,3);
            editCompanyLayout.addComponent(bLayout,0,4);
            
            //adding the createCompanyLayout to the mainLayout
            mainlayout.addComponent(editCompanyLayout);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            companyNameField.clear();
            companyWebsiteField.clear();
            companyDetailsField.clear();
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_companies && selectedCompany != null) {
                    administration = new Administration(user.user_group, user.company);
                    
                    companyNameField.setValue(selectedCompany.name);
                    if (selectedCompany.website != null) companyWebsiteField.setValue(selectedCompany.website);
                    if (selectedCompany.details != null) companyDetailsField.setValue(selectedCompany.details);
                } else {
                    nav.navigateTo(manageUsersView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //EditCompaniesLayout ends
    
    //ManageApplicationLayout start
    public class ManageApplicationLayout extends GridLayout implements View {
        public Administration administration = new Administration();
        
        public final String HIDDEN_COLUMN_IDENTIFIER = "id";
        public final String APPLICATION_NAME_COLUMN_IDENTIFIER = "Name";
        public final String UPDATE_INTERVAL_COLUMN_IDENTIFIER = "Update interval";
        public final String LATEST_UPDATE_COLUMN_IDENTIFIER = "Latest update";
        public final String COMPANY_COLUMN_IDENTIFIER = "Company";
        
        public final IndexedContainer tableContainer = new IndexedContainer();
        public final Grid applicationTable = new Grid(tableContainer);
        
        public final Window subConfirmWindow = new Window();
        
        public ManageApplicationLayout(){
            
            this.tableContainer.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.APPLICATION_NAME_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.UPDATE_INTERVAL_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.LATEST_UPDATE_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.COMPANY_COLUMN_IDENTIFIER, String.class, null);
            
            this.applicationTable.getColumn(this.HIDDEN_COLUMN_IDENTIFIER).setHidden(true);
            this.applicationTable.setSelectionMode(SelectionMode.SINGLE);
            
            setWidth("100%");
            setHeight("100%");

            final GridLayout applicationLayout = new GridLayout(1,4);
            applicationLayout.setWidth("90%");
            applicationLayout.setHeight("90%");
            addComponent(applicationLayout);
            setComponentAlignment(applicationLayout, Alignment.MIDDLE_CENTER);

            Label testLbl = new Label("Applications");
            applicationLayout.addComponent(testLbl,0,0);
            applicationLayout.addComponent(applicationTable, 0, 1);
            applicationLayout.setColumnExpandRatio(0, 1);
            applicationLayout.setRowExpandRatio(1,1);
            applicationTable.setSizeFull();
            
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
            
            Button createApplicationButton = new Button("Create application");
            createApplicationButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    CompanyRow[] companies;
                    if ((companies = administration.object_collections.companies()) != null &&
                         companies.length >= 1 &&
                         companies[0] != null) {
                        
                        tableContainer.removeAllItems();
                        nav.navigateTo(createApplicationView);
                    } else {
                        Notification.show("No user company found in the database.\n" +
                                          "Please create a company before attempting to create a application.", Notification.Type.WARNING_MESSAGE);
                    }
                }
            });

            Button editApplicationButton = new Button("Edit application");
            editApplicationButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) applicationTable.getSelectionModel()).getSelectedRow();

                    if (selected != null) {
                        selectedApplication = new ApplicationRow(selected.toString());
                        System.out.println("Selected application id: " + selectedApplication.id);
                        tableContainer.removeAllItems();
                        nav.navigateTo(editApplicationView);
                    } else {
                        System.out.println("Nothing selected.");
                    }
                }
            });
            
            Button deleteCompanyButton = new Button("Delete application");
            deleteCompanyButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) applicationTable.getSelectionModel()).getSelectedRow();
                    
                    if (selected != null) {
                        selectedApplication = new ApplicationRow(selected.toString(), false);
                        subConfirmWindow.setHeight(250,Unit.PIXELS);
                        subConfirmWindow.setWidth(350,Unit.PIXELS);
                        subConfirmWindow.setResizable(false);

                        GridLayout subConfirmLayout = new GridLayout(1,4);
                        subConfirmLayout.setMargin(true);
                        subConfirmLayout.setSizeFull();
                        subConfirmWindow.setContent(subConfirmLayout);

                        // Put some components in it
                        Label titelConfirmWindow = new Label("Delete company");
                        subConfirmLayout.addComponent(titelConfirmWindow, 0, 0);
                        Label contentConfirmWindow = new Label("You are about to delete "+selectedApplication.name+"!\nAre you sure?");
                        subConfirmLayout.addComponent(contentConfirmWindow, 0, 1);
                        
                        Button confirmButton = new Button("Yes");
                        confirmButton.addClickListener(new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                
                                GridLayout subExtraConfirmLayout = new GridLayout(1,3);
                                subExtraConfirmLayout.setMargin(true);
                                subExtraConfirmLayout.setSizeFull();
                                subConfirmWindow.setContent(subExtraConfirmLayout);
                                
                                Label ConfirmWindowLabel = new Label("This will delete all configs ,\n"
                                                                    +"and logs bound to "+selectedApplication.name);
                                subExtraConfirmLayout.addComponent(ConfirmWindowLabel, 0, 0);
                                Label contentConfirmWindow = new Label("\nAre you sure?");
                                subExtraConfirmLayout.addComponent(contentConfirmWindow, 0, 1);
                                
                                Button confirmExtra = new Button("Yes");
                                confirmExtra.addClickListener(new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(Button.ClickEvent event) {
                                        if (administration.application.remove(selectedApplication)) {
                                            tableContainer.removeItem(selected);
                                            subConfirmWindow.close();
                                        } else {
                                        }
                                    }
                                });
                                
                                Button declineButton = new Button("No");
                                declineButton.addClickListener(new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(Button.ClickEvent event) {
                                        subConfirmWindow.close();
                                    }
                                });
                                HorizontalLayout bLayout = new HorizontalLayout();
                                bLayout.addComponent(confirmExtra);
                                bLayout.addComponent(declineButton);
                        
                                 subExtraConfirmLayout.addComponent(bLayout, 0, 2);
                                // Center it in the browser window
                                subConfirmWindow.center();

                                // Open it in the UI
                                
//                                addWindow(subConfirmWindow);
//                                
                            }
                        });
                                
                        Button declineButton = new Button("No");
                        declineButton.addClickListener(new Button.ClickListener() {
                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                subConfirmWindow.close();
                            }
                        });
                        
                        //Adding buttons to the window
                        HorizontalLayout bLayout = new HorizontalLayout();
                        bLayout.addComponent(confirmButton);
                        bLayout.addComponent(declineButton);
                        
                        subConfirmLayout.addComponent(bLayout, 0, 2);
                        // Center it in the browser window
                        subConfirmWindow.center();

                        // Open it in the UI
                        addWindow(subConfirmWindow);
                    
                            //If user removed
                    } else {
                            //If user not removed
                    }
                }
            });
            
            //Adding the buttons to the layout
            buttonLayout.addComponent(createApplicationButton);
            buttonLayout.addComponent(editApplicationButton);
            buttonLayout.addComponent(deleteCompanyButton);
            buttonLayout.addComponent(back);
            applicationLayout.addComponent(buttonLayout, 0, 2);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;

            applicationTable.deselectAll();
            tableContainer.removeAllItems();
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_applications) {

                    administration = new Administration(user.user_group, user.company);
                    
                    ApplicationRow[] found_applications;
                    if ((found_applications = administration.object_collections.applications()) != null) {
                        for (ApplicationRow applicationRow : found_applications) {
                            Item userItem = tableContainer.addItem(applicationRow.id);
                            userItem.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(applicationRow.id);
                            userItem.getItemProperty(this.APPLICATION_NAME_COLUMN_IDENTIFIER).setValue(applicationRow.name);
                            userItem.getItemProperty(this.UPDATE_INTERVAL_COLUMN_IDENTIFIER).setValue(applicationRow.update_interval);
                            userItem.getItemProperty(this.LATEST_UPDATE_COLUMN_IDENTIFIER).setValue(applicationRow.latest_update);
                            userItem.getItemProperty(this.COMPANY_COLUMN_IDENTIFIER).setValue(applicationRow.company.name);
                        }
                    }
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //ManageApplicationLayout ends
    
    //CreateApplicationLayout start
    public class CreateApplicationLayout extends GridLayout implements View {
        
        public final String NAME_COLUMN_IDENTIFIER = "Name";
        
        public Administration administration = new Administration();
        
        public TextField applicationNameField = new TextField("Application name");
        public TextField uppdateIntevalField = new TextField("Update inteval");
        
        public final IndexedContainer company_container = new IndexedContainer();
        public final ComboBox company_name = new ComboBox("Company", company_container);
        
        public Label application_Name_Label = new Label();
        public Label application_update_interval_Label = new Label();
        public Label application_Company_Label = new Label();
        
        public CreateApplicationLayout() {
            
            this.company_container.addContainerProperty(this.NAME_COLUMN_IDENTIFIER, String.class, null);
            
            this.company_name.setItemCaptionPropertyId(this.NAME_COLUMN_IDENTIFIER);
            this.company_name.setNullSelectionAllowed(false);
            this.company_name.setTextInputAllowed(false);
            
            application_Name_Label.setVisible(false);
            application_update_interval_Label.setVisible(false);
            application_Company_Label.setVisible(false);
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout createApplicationLayout = new GridLayout(2,5);
            createApplicationLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            // components for the layout
            Label createApplicationTitel = new Label("Create a new application");
            
            Button backButton = new Button("Back");
            backButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    nav.navigateTo(manageApplicationView);
                }
            });
            
            Button createCompanyButton = new Button("Create");
            createCompanyButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    administration.application.settings.clear_error_messages();
                    
                    application_Name_Label.setVisible(false);
                    application_update_interval_Label.setVisible(false);
                    application_Company_Label.setVisible(false);
                    
                    if (administration.application.create(
                            company_name.getValue().toString(),
                            applicationNameField.getValue(),
                            null,
                            uppdateIntevalField.getValue().toString()
                    ) != null) {
                        System.out.println("Application created.");
                        Notification.show("\tApplication created\t", Notification.TYPE_HUMANIZED_MESSAGE);
                    } else {
                        System.out.println("Application not created.");

                        if (administration.application.settings.NAME_ERROR_MESSAGE != null) {
                            application_Name_Label.setValue(administration.application.settings.NAME_ERROR_MESSAGE);
                            if (!application_Name_Label.isVisible()) application_Name_Label.setVisible(true);
                        }

                        if (administration.application.settings.UPDATE_INTERVAL_ERROR_MESSAGE != null) {
                            application_update_interval_Label.setValue(administration.application.settings.UPDATE_INTERVAL_ERROR_MESSAGE);
                            if (!application_update_interval_Label.isVisible()) application_update_interval_Label.setVisible(true);
                        }

                        if (administration.application.settings.COMPANY_ERROR_MESSAGE != null) {
                            application_Company_Label.setValue(administration.application.settings.COMPANY_ERROR_MESSAGE);
                            if (!application_Company_Label.isVisible()) application_Company_Label.setVisible(true);
                        }
                    }
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
            createApplicationLayout.addComponent(createApplicationTitel,0,0);
            createApplicationLayout.addComponent(applicationNameField,0,1);
            createApplicationLayout.addComponent(application_Name_Label,1,1);
            createApplicationLayout.addComponent(uppdateIntevalField,0,2);
            createApplicationLayout.addComponent(application_update_interval_Label,1,2);
            createApplicationLayout.addComponent(company_name,0,3);
            createApplicationLayout.addComponent(application_Company_Label,1,3);
            createApplicationLayout.addComponent(bLayout,0,4);
            
            //adding the createCompanyLayout to the mainLayout
            mainlayout.addComponent(createApplicationLayout);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            applicationNameField.clear();
            company_container.removeAllItems();
            uppdateIntevalField.clear();
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_applications) {
                    administration = new Administration(user.user_group, user.company);
                     
                   
                    for (CompanyRow company : administration.object_collections.companies()) {
                        Item newItem = company_container.addItem(company.id);
                        newItem.getItemProperty(this.NAME_COLUMN_IDENTIFIER).setValue(company.name);
                    }
                    company_name.setValue(company_name.getItemIds().toArray()[0]);
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //CreateApplicationLayout end
    
    //EditApplicationLayout start
    public class EditApplicationLayout extends GridLayout implements View {
        
        public final String NAME_COLUMN_IDENTIFIER = "Name";
        
        public Administration administration = new Administration();
        
        public TextField applicationNameField = new TextField("Application name");
        public TextField uppdateIntevalField = new TextField("Update inteval in sec");
        
        public final IndexedContainer company_container = new IndexedContainer();
        public final ComboBox company_name = new ComboBox("Company", company_container);
        
        public Label application_Name_Label = new Label();
        public Label application_update_interval_Label = new Label();
        public Label application_Company_Label = new Label();
        
        public EditApplicationLayout(){
        this.company_container.addContainerProperty(this.NAME_COLUMN_IDENTIFIER, String.class, null);
            
            this.company_name.setItemCaptionPropertyId(this.NAME_COLUMN_IDENTIFIER);
            this.company_name.setNullSelectionAllowed(false);
            this.company_name.setTextInputAllowed(false);
            
            application_Name_Label.setVisible(false);
            application_update_interval_Label.setVisible(false);
            application_Company_Label.setVisible(false);
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout createApplicationLayout = new GridLayout(2,5);
            createApplicationLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            // components for the layout
            Label editApplicationTitel = new Label("Edit application");
            
            Button backButton = new Button("Back");
            backButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){
                    applicationNameField.clear();
                    uppdateIntevalField.clear();
                    nav.navigateTo(manageApplicationView);
                }
            });
            
            Button editCompanyButton = new Button("Done");
            editCompanyButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    administration.application.settings.clear_error_messages();
                    
                    application_Name_Label.setVisible(false);
                    application_update_interval_Label.setVisible(false);
                    application_Company_Label.setVisible(false);
                    
                    if (administration.application.edit(
                            selectedApplication,
                            company_name.getValue().toString(),
                            applicationNameField.getValue(),
                            null,
                            uppdateIntevalField.getValue().toString()
                    )) {
                        System.out.println("Application changed.");
                        Notification.show("\tApplication changed\t", Notification.TYPE_HUMANIZED_MESSAGE);
                    } else {
                        System.out.println("Application not changed.");

                        if (administration.application.settings.NAME_ERROR_MESSAGE != null) {
                            application_Name_Label.setValue(administration.application.settings.NAME_ERROR_MESSAGE);
                            if (!application_Name_Label.isVisible()) application_Name_Label.setVisible(true);
                        }

                        if (administration.application.settings.UPDATE_INTERVAL_ERROR_MESSAGE != null) {
                            application_update_interval_Label.setValue(administration.application.settings.UPDATE_INTERVAL_ERROR_MESSAGE);
                            if (!application_update_interval_Label.isVisible()) application_update_interval_Label.setVisible(true);
                        }

                        if (administration.application.settings.COMPANY_ERROR_MESSAGE != null) {
                            application_Company_Label.setValue(administration.application.settings.COMPANY_ERROR_MESSAGE);
                            if (!application_Company_Label.isVisible()) application_Company_Label.setVisible(true);
                        }
                    }
                }
            });
            
            //creating a layout for the buttons
            HorizontalLayout bLayout = new HorizontalLayout();
            bLayout.setStyleName("top_padding");
            bLayout.setWidth("100%");
            bLayout.setHeight("100%");
            
            //adding the buttons to the buttonlayout
            bLayout.addComponent(editCompanyButton);
            bLayout.addComponent(backButton);
            bLayout.setComponentAlignment(editCompanyButton, Alignment.MIDDLE_LEFT);
            bLayout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT);
            
            //adding the componets to the grid
            createApplicationLayout.addComponent(editApplicationTitel,0,0);
            createApplicationLayout.addComponent(applicationNameField,0,1);
            createApplicationLayout.addComponent(application_Name_Label,1,1);
            createApplicationLayout.addComponent(uppdateIntevalField,0,2);
            createApplicationLayout.addComponent(application_update_interval_Label,1,2);
            createApplicationLayout.addComponent(company_name,0,3);
            createApplicationLayout.addComponent(application_Company_Label,1,3);
            createApplicationLayout.addComponent(bLayout,0,4);
            
            //adding the createCompanyLayout to the mainLayout
            mainlayout.addComponent(createApplicationLayout);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            applicationNameField.clear();
            company_container.removeAllItems();
            uppdateIntevalField.clear();
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_applications) {
                    
                    administration = new Administration(user.user_group, user.company);
                    
                    applicationNameField.setValue(selectedApplication.name);
                    uppdateIntevalField.setValue(selectedApplication.update_interval);
                    
                    if (selectedApplication.company.id.equals("1")) {
                        Item newItem = company_container.addItem(selectedApplication.company.id);
                        newItem.getItemProperty(this.NAME_COLUMN_IDENTIFIER).setValue(selectedApplication.company.name);
                    } else {
                        for (CompanyRow company : administration.object_collections.companies()) {
                            Item newItem = company_container.addItem(company.id);
                            newItem.getItemProperty(this.NAME_COLUMN_IDENTIFIER).setValue(company.name);
                        }
                    }
                    
                    company_name.setValue(selectedApplication.company.id);
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //EditApplicationLayout end
    
    //ManageUserGroup start
    public class ManageUserGroupLayout extends GridLayout implements View {
        public Administration administration = new Administration();
        
        public final String HIDDEN_COLUMN_IDENTIFIER = "id";
        public final String NAME_COLUMN_IDENTIFIER = "Name";
        public final String VIEW_LOGS_COLUMN_IDENTIFIER = "View logs access";
        public final String MANAGE_APPLICATIONS_COLUMN_IDENTIFIER = "Manage application access";
        public final String MANAGE_USERS_COLUMN_IDENTIFIER = "Manage users access";
        public final String MANAGE_COMPANIES_COLUMN_IDENTIFIER = "Manage companies access";
        public final String MANAGE_GROUPS_COLUMN_IDENTIFIER = "Manage groups access";
        
        public final IndexedContainer tableContainer = new IndexedContainer();
        public final Grid userGroupTable = new Grid(tableContainer);
        
        public final Window subConfirmWindow = new Window();
        
        public ManageUserGroupLayout(){
            
            this.tableContainer.addContainerProperty(this.HIDDEN_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.NAME_COLUMN_IDENTIFIER, String.class, null);
            this.tableContainer.addContainerProperty(this.VIEW_LOGS_COLUMN_IDENTIFIER, Boolean.class, null);
            this.tableContainer.addContainerProperty(this.MANAGE_APPLICATIONS_COLUMN_IDENTIFIER, Boolean.class, null);
            this.tableContainer.addContainerProperty(this.MANAGE_USERS_COLUMN_IDENTIFIER, Boolean.class, null);
            this.tableContainer.addContainerProperty(this.MANAGE_COMPANIES_COLUMN_IDENTIFIER, Boolean.class, null);
            this.tableContainer.addContainerProperty(this.MANAGE_GROUPS_COLUMN_IDENTIFIER, Boolean.class, null);
            
            this.userGroupTable.getColumn(this.HIDDEN_COLUMN_IDENTIFIER).setHidden(true);
            this.userGroupTable.setSelectionMode(SelectionMode.SINGLE);
            
            setWidth("100%");
            setHeight("100%");

            final GridLayout UserGroupsLayout = new GridLayout(1,4);
            UserGroupsLayout.setWidth("90%");
            UserGroupsLayout.setHeight("90%");
            addComponent(UserGroupsLayout);
            setComponentAlignment(UserGroupsLayout, Alignment.MIDDLE_CENTER);

            Label testLbl = new Label("User groups");
            UserGroupsLayout.addComponent(testLbl,0,0);
            UserGroupsLayout.addComponent(userGroupTable, 0, 1);
            UserGroupsLayout.setColumnExpandRatio(0, 1);
            UserGroupsLayout.setRowExpandRatio(1,1);
            userGroupTable.setSizeFull();
            
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
            
            Button createUserGroupButton = new Button("Create user group");
            createUserGroupButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    tableContainer.removeAllItems();
                    nav.navigateTo(createUserGroupsView);
                }
            });

            Button editUserGroupButton = new Button("Edit user group");
            editUserGroupButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) userGroupTable.getSelectionModel()).getSelectedRow();

                    if (selected != null) {
                        selectedUserGroup = new UserGroups(selected.toString());
                        if (!administration.user_group.settings.ADMINISTRATOR_USER_GROUP.equals(selectedUserGroup.id)) {
                            tableContainer.removeAllItems();
                            nav.navigateTo(editUserGroupsView);
                        } else {
                            Notification.show("Editing this user group is not allowed!", Notification.Type.WARNING_MESSAGE);
                        }
                    } else {
                        System.out.println("Nothing selected.");
                    }
                }
            });
            
            Button deleteUserGroupButton = new Button("Delete user group");
            deleteUserGroupButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    Object selected = ((SingleSelectionModel) userGroupTable.getSelectionModel()).getSelectedRow();
                    
                    if (selected != null) {
                        selectedUserGroup = new UserGroups(selected.toString());
                        
                        if (!administration.user_group.settings.ADMINISTRATOR_USER_GROUP.equals(selectedUserGroup.id)) {

                            subConfirmWindow.setHeight(250,Unit.PIXELS);
                            subConfirmWindow.setWidth(350,Unit.PIXELS);
                            subConfirmWindow.setResizable(false);

                            GridLayout subConfirmLayout = new GridLayout(1,4);
                            subConfirmLayout.setMargin(true);
                            subConfirmLayout.setSizeFull();
                            subConfirmWindow.setContent(subConfirmLayout);

                            // Put some components in it
                            Label titelConfirmWindow = new Label("Delete user group");
                            subConfirmLayout.addComponent(titelConfirmWindow, 0, 0);
                            Label contentConfirmWindow = new Label("You are about to delete "+selectedUserGroup.name+"!\nAre you sure?");
                            subConfirmLayout.addComponent(contentConfirmWindow, 0, 1);

                            Button confirmButton = new Button("Yes");
                            confirmButton.addClickListener(new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent event) {

                                    GridLayout subExtraConfirmLayout = new GridLayout(1,3);
                                    subExtraConfirmLayout.setMargin(true);
                                    subExtraConfirmLayout.setSizeFull();
                                    subConfirmWindow.setContent(subExtraConfirmLayout);

                                    Label ConfirmWindowLabel = new Label("This will also delete all \n"
                                                                        +"users that are still part of "+selectedUserGroup.name);
                                    subExtraConfirmLayout.addComponent(ConfirmWindowLabel, 0, 0);
                                    Label contentConfirmWindow = new Label("\nAre you sure?");
                                    subExtraConfirmLayout.addComponent(contentConfirmWindow, 0, 1);

                                    Button confirmExtra = new Button("Yes");
                                    confirmExtra.addClickListener(new Button.ClickListener() {
                                        @Override
                                        public void buttonClick(Button.ClickEvent event) {
                                            if (administration.user_group.remove(selectedUserGroup.id)) {
                                                tableContainer.removeItem(selected);
                                                subConfirmWindow.close();
                                            } else {
                                            }
                                        }
                                    });

                                    Button declineButton = new Button("No");
                                    declineButton.addClickListener(new Button.ClickListener() {
                                        @Override
                                        public void buttonClick(Button.ClickEvent event) {
                                            subConfirmWindow.close();
                                        }
                                    });
                                    HorizontalLayout bLayout = new HorizontalLayout();
                                    bLayout.addComponent(confirmExtra);
                                    bLayout.addComponent(declineButton);

                                     subExtraConfirmLayout.addComponent(bLayout, 0, 2);
                                    // Center it in the browser window
                                    subConfirmWindow.center();

                                    // Open it in the UI

                                }
                            });

                            Button declineButton = new Button("No");
                            declineButton.addClickListener(new Button.ClickListener() {
                                @Override
                                public void buttonClick(Button.ClickEvent event) {
                                    subConfirmWindow.close();
                                }
                            });

                            //Adding buttons to the window
                            HorizontalLayout bLayout = new HorizontalLayout();
                            bLayout.addComponent(confirmButton);
                            bLayout.addComponent(declineButton);

                            subConfirmLayout.addComponent(bLayout, 0, 2);
                            // Center it in the browser window
                            subConfirmWindow.center();

                            // Open it in the UI
                            addWindow(subConfirmWindow);
                    
                        } else {
                            Notification.show("Removing this user group is not allowed!", Notification.Type.WARNING_MESSAGE);
                        }
                    } else {
                            //If user not removed
                    }
                }
            });
            
            //Adding the buttons to the layout
            buttonLayout.addComponent(createUserGroupButton);
            buttonLayout.addComponent(editUserGroupButton);
            buttonLayout.addComponent(deleteUserGroupButton);
            buttonLayout.addComponent(back);
            UserGroupsLayout.addComponent(buttonLayout, 0, 2);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            userGroupTable.deselectAll();
            tableContainer.removeAllItems();
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_groups) {
                    
                    administration = new Administration(user.user_group, user.company);
                    
                    for (UserGroups UserRow : administration.object_collections.user_groups()) {
                        Item userItem = tableContainer.addItem(UserRow.id);
                        userItem.getItemProperty(this.HIDDEN_COLUMN_IDENTIFIER).setValue(UserRow.id);
                        userItem.getItemProperty(this.NAME_COLUMN_IDENTIFIER).setValue(UserRow.name);
                        userItem.getItemProperty(this.VIEW_LOGS_COLUMN_IDENTIFIER).setValue(UserRow.view_logs);
                        userItem.getItemProperty(this.MANAGE_APPLICATIONS_COLUMN_IDENTIFIER).setValue(UserRow.manage_applications);
                        userItem.getItemProperty(this.MANAGE_USERS_COLUMN_IDENTIFIER).setValue(UserRow.manage_users);
                        userItem.getItemProperty(this.MANAGE_COMPANIES_COLUMN_IDENTIFIER).setValue(UserRow.manage_companies);
                        userItem.getItemProperty(this.MANAGE_GROUPS_COLUMN_IDENTIFIER).setValue(UserRow.manage_groups);
                    }
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //ManageUserGroup end
    
    //CreateUserGroups start
    public class CreateUserGroupLayout extends GridLayout implements View {
        
        public Administration administration = new Administration();
        
        public TextField userGroupNameField = new TextField("User group name");
        
        public CheckBox viewLogsCheckbox = new CheckBox("View logs access"); 
        public CheckBox manageApplicationCheckbox = new CheckBox("Manage application access"); 
        public CheckBox manageUsersCheckbox = new CheckBox("Manage users access"); 
        public CheckBox manageCompaniesCheckbox = new CheckBox("Manage companies access"); 
        public CheckBox manageUserGroupsCheckbox = new CheckBox("Manage user groups access"); 
        
        public Label create_User_Groups_Name_Label = new Label();
        
        public CreateUserGroupLayout() {
            
            create_User_Groups_Name_Label.setVisible(false);
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout createUserGroupsLayout = new GridLayout(2,9);
            createUserGroupsLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            // components for the layout
            Label createUserGroupsTitel = new Label("Create a new user group");
            
            Button backButton = new Button("Back");
            backButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    nav.navigateTo(manageUserGroupsView);
                }
            });
            
            Button createUserGroupButton = new Button("Create");
            createUserGroupButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    administration.user_group.settings.clear_error_messages();
                    
                    create_User_Groups_Name_Label.setVisible(false);
                    
                    if (administration.user_group.create(
                            userGroupNameField.getValue(),
                            viewLogsCheckbox.getValue(),
                            manageApplicationCheckbox.getValue(),
                            manageUsersCheckbox.getValue(),
                            manageCompaniesCheckbox.getValue(),
                            manageUserGroupsCheckbox.getValue()
                    )){
                        System.out.println("User group created.");
                        Notification.show("\tUser group created\t", Notification.TYPE_HUMANIZED_MESSAGE);
                    } else {
                        System.out.println("user group not created.");

                        if (administration.user_group.settings.NAME_ERROR_MESSAGE != null) {
                            create_User_Groups_Name_Label.setValue(administration.user_group.settings.NAME_ERROR_MESSAGE);
                            if (!create_User_Groups_Name_Label.isVisible()) create_User_Groups_Name_Label.setVisible(true);
                        }
                    }
                }
            });
            
            //creating a layout for the buttons
            HorizontalLayout bLayout = new HorizontalLayout();
            bLayout.setStyleName("top_padding");
            bLayout.setWidth("100%");
            bLayout.setHeight("100%");
            
            //adding the buttons to the buttonlayout
            bLayout.addComponent(createUserGroupButton);
            bLayout.addComponent(backButton);
            bLayout.setComponentAlignment(createUserGroupButton, Alignment.MIDDLE_LEFT);
            bLayout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT);
            
            //adding the componets to the grid
            createUserGroupsLayout.addComponent(createUserGroupsTitel,0,0);
            createUserGroupsLayout.addComponent(userGroupNameField,0,1);
            userGroupNameField.addStyleName("apps_padding");
            createUserGroupsLayout.addComponent(create_User_Groups_Name_Label,1,1);
            createUserGroupsLayout.addComponent(viewLogsCheckbox,0,2);
            viewLogsCheckbox.addStyleName("apps_padding");
            createUserGroupsLayout.addComponent(manageApplicationCheckbox,0,3);
            manageApplicationCheckbox.addStyleName("apps_padding");
            createUserGroupsLayout.addComponent(manageUsersCheckbox,0,4);
            manageUsersCheckbox.addStyleName("apps_padding");
            createUserGroupsLayout.addComponent(manageCompaniesCheckbox,0,5);
            manageCompaniesCheckbox.addStyleName("apps_padding");
            createUserGroupsLayout.addComponent(manageUserGroupsCheckbox,0,6);
            manageUserGroupsCheckbox.addStyleName("apps_padding");
            createUserGroupsLayout.addComponent(bLayout,0,7);
            
            //adding the createCompanyLayout to the mainLayout
            mainlayout.addComponent(createUserGroupsLayout);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            userGroupNameField.clear();
            viewLogsCheckbox.setValue(true);
            manageApplicationCheckbox.setValue(false);
            manageUsersCheckbox.setValue(false);
            manageCompaniesCheckbox.setValue(false);
            manageUserGroupsCheckbox.setValue(false);
            
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_groups) {
                    administration = new Administration(user.user_group, user.company);
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //CreateUserGroups ends
    
    //EditUserGroupLayout start
    public class EditUserGroupLayout extends GridLayout implements View {
        
        public Administration administration = new Administration();
        
        public TextField userGroupNameField = new TextField("User group name");
        
        public CheckBox viewLogsCheckbox = new CheckBox("View logs access"); 
        public CheckBox manageApplicationCheckbox = new CheckBox("Manage application access"); 
        public CheckBox manageUsersCheckbox = new CheckBox("Manage users access"); 
        public CheckBox manageCompaniesCheckbox = new CheckBox("Manage companies access"); 
        public CheckBox manageUserGroupsCheckbox = new CheckBox("Manage user groups access"); 
        
        public Label edit_User_Groups_Name_Label = new Label();
        
        public EditUserGroupLayout() {
            
            edit_User_Groups_Name_Label.setVisible(false);
            
            setWidth("100%");
            setHeight("100%");
            
            HorizontalLayout mainlayout = new HorizontalLayout();
            GridLayout editUserGroupsLayout = new GridLayout(2,9);
            editUserGroupsLayout.setStyleName("login-grid-layout");
            addComponent(mainlayout);
            setComponentAlignment(mainlayout, Alignment.MIDDLE_CENTER);
            
            // components for the layout
            Label editUserGroupsTitel = new Label("Edit a user group");
            
            Button backButton = new Button("Back");
            backButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event){ 
                    nav.navigateTo(manageUserGroupsView);
                }
            });
            
            Button editUserGroupButton = new Button("Done");
            editUserGroupButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    administration.user_group.settings.clear_error_messages();
                    
                    edit_User_Groups_Name_Label.setVisible(false);
                    
                    if (administration.user_group.edit(
                            selectedUserGroup,
                            userGroupNameField.getValue(),
                            viewLogsCheckbox.getValue(),
                            manageApplicationCheckbox.getValue(),
                            manageUsersCheckbox.getValue(),
                            manageCompaniesCheckbox.getValue(),
                            manageUserGroupsCheckbox.getValue()
                    )){
                        System.out.println("User group edited.");
                        Notification.show("\tUser group edited\t", Notification.TYPE_HUMANIZED_MESSAGE);
                    } else {
                        System.out.println("user group not edited.");

                        if (administration.user_group.settings.NAME_ERROR_MESSAGE != null) {
                            edit_User_Groups_Name_Label.setValue(administration.user_group.settings.NAME_ERROR_MESSAGE);
                            if (!edit_User_Groups_Name_Label.isVisible()) edit_User_Groups_Name_Label.setVisible(true);
                        }
                    }
                }
            });
            
            //creating a layout for the buttons
            HorizontalLayout bLayout = new HorizontalLayout();
            bLayout.setStyleName("top_padding");
            bLayout.setWidth("100%");
            bLayout.setHeight("100%");
            
            //adding the buttons to the buttonlayout
            bLayout.addComponent(editUserGroupButton);
            bLayout.addComponent(backButton);
            bLayout.setComponentAlignment(editUserGroupButton, Alignment.MIDDLE_LEFT);
            bLayout.setComponentAlignment(backButton, Alignment.MIDDLE_RIGHT);
            
            //adding the componets to the grid
            editUserGroupsLayout.addComponent(editUserGroupsTitel,0,0);
            editUserGroupsLayout.addComponent(userGroupNameField,0,1);
            editUserGroupsLayout.addComponent(edit_User_Groups_Name_Label,1,1);
            editUserGroupsLayout.addComponent(viewLogsCheckbox,0,2);
            editUserGroupsLayout.addComponent(manageApplicationCheckbox,0,3);
            editUserGroupsLayout.addComponent(manageUsersCheckbox,0,4);
            editUserGroupsLayout.addComponent(manageCompaniesCheckbox,0,5);
            editUserGroupsLayout.addComponent(manageUserGroupsCheckbox,0,6);
            editUserGroupsLayout.addComponent(bLayout,0,7);
            
            //adding the createCompanyLayout to the mainLayout
            mainlayout.addComponent(editUserGroupsLayout);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            current_view = this;
            
            userGroupNameField.clear();
            viewLogsCheckbox.setValue(false);
            manageApplicationCheckbox.setValue(false);
            manageUsersCheckbox.setValue(false);
            manageCompaniesCheckbox.setValue(false);
            manageUserGroupsCheckbox.setValue(false);
            
            if (user != null && user.is_authenticated) {
                if (user.user_group.manage_groups) {
                    administration = new Administration(user.user_group, user.company);
                    
                    userGroupNameField.setValue(selectedUserGroup.name);
                    viewLogsCheckbox.setValue(selectedUserGroup.view_logs);
                    manageApplicationCheckbox.setValue(selectedUserGroup.manage_applications);
                    manageUsersCheckbox.setValue(selectedUserGroup.manage_users);
                    manageCompaniesCheckbox.setValue(selectedUserGroup.manage_companies);
                    manageUserGroupsCheckbox.setValue(selectedUserGroup.manage_groups);
                    
                } else {
                    nav.navigateTo(logsView);
                }
            } else {
                nav.navigateTo(loginView);
            }
        }
    }
    //EditUserGroupLayout end
    
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
    
    @Override
    public void refresh(VaadinRequest request) {
        refresh_logs_view();
    }
    
    public void refresh_logs_view() {
        if (current_view instanceof ViewLogsLayout && user.is_authenticated) {
            user.load_available_applications();
            nav.addView(logsView, new ViewLogsLayout());
            nav.navigateTo(logsView);
        }
    }
}
