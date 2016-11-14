package logaggregatorclient;

import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class GUI extends Application {
    
    private String username = null;
    private String password = null;

    public static final String COMBOBOX_HOURS = "Hours";
    public static final String COMBOBOX_MINUTES = "Minutes";
    public static final String COMBOBOX_SECONDS = "Seconds";
    
    private final Connections connections = new Connections();
    
    @Override
    public void start(Stage primaryStage) {
        /*
        This method contains the UI that will initialize when the 
        application starts.
        
        Maybe a login window or something similar could be created here.
        */
        Configurations.readPropertiesFile();
        
        if (Configurations.client_configurations_found) {
            primaryStage.setScene(betaMenu(primaryStage));
        } else {
            primaryStage.setScene(noConfigScene(primaryStage));
        }
        
        primaryStage.show();
    }
    
    private Scene betaMenu(Stage primaryStage) {
        connections.checkConfigurations();
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text titel = new Text("what do you want to do");
        grid.add(titel, 0, 0);
        
        Button next = new Button("To login");
        grid.add(next, 1, 1);
        next.setOnAction(e-> primaryStage.setScene(login(primaryStage)));
        
        
        Button update = new Button("Update log");
        grid.add(update, 0, 1);
        update.setOnAction(e -> betaAutomaticUpdates(primaryStage));
        
        return new Scene(grid);
    }
    
    private Scene startupMenu(Stage primaryStage) {
        Configurations.Application[] applications = Configurations.getApplicationConfigurations();
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text title = new Text("Logs on this device.");
        grid.add(title, 0, 0, 5, 1);
        
        TableView<Configurations.Application> table = new TableView();
        
        TableColumn application_name_column = new TableColumn("Application name");
        TableColumn update_interval_column = new TableColumn("Update interval");
        TableColumn local_source_column = new TableColumn("Local source");
        
        
        
        return new Scene(grid);
    }
    
    private Scene login(Stage primaryStage){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text login_title = new Text("Welcome");
        login_title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(login_title, 0, 0, 2, 1);
        
        grid.add(new Label("Username:"), 0, 1);
        grid.add(new Label("Password:"), 0, 2);
        
        TextField username_textfield = new TextField();
        grid.add(username_textfield, 1, 1);
        
        PasswordField password_passwordfield = new PasswordField();
        grid.add(password_passwordfield, 1, 2);
        
        Button login_button = new Button("Login");
        HBox login_button_hbox = new HBox(10);
        login_button_hbox.setAlignment(Pos.BOTTOM_RIGHT);
        login_button_hbox.getChildren().add(login_button);
        grid.add(login_button_hbox, 1, 4);
        
        login_button.setOnAction(e ->
                this.loginCheck(username_textfield.getText(),
                                password_passwordfield.getText(),
                                primaryStage)
        );
        
        Scene login = new Scene(grid, 350, 270);
        
        return login;
    }
    
    private Scene app(Stage primaryStage) {
        final FileChooser fileChooser = new FileChooser();
        final ComboBox interval = new ComboBox();
        interval.getItems().addAll(COMBOBOX_HOURS,
                                   COMBOBOX_MINUTES,
                                   COMBOBOX_SECONDS);
        interval.getSelectionModel().selectFirst();
        
        primaryStage.setTitle("Register service");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Label app_name = new Label("App name");
        grid.add(app_name, 0, 0);
        Label inter = new Label("Interval");
        grid.add(inter, 0, 4);      
        Label servname = new Label("Source");
        grid.add(servname, 0, 2);
        
        TextField textfield_application_name = new TextField();
        grid.add(textfield_application_name,0,1);
        TextField textfield_application_source = new TextField();
        TextField textfield_update_interval = new TextField();
        grid.add(textfield_application_source,0,3);
        grid.add(textfield_update_interval,0,5);
        grid.add(interval,1,5);
        
        Button browse = new Button("Browse");
        grid.add(browse, 1, 3);
        browse.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(final ActionEvent e){
                File logfile = fileChooser.showOpenDialog(primaryStage);
                if (logfile != null){
//                  texser = logfile.getAbsolutePath();
                  textfield_application_source.setText(logfile.getAbsolutePath());
                }
                
            }
        });
        
        Button exportbtn = new Button("Export");
        grid.add(exportbtn, 0, 6);
        exportbtn.setOnAction(e ->
                this.newApplication(primaryStage,
                                    textfield_application_name.getText(),
                                    textfield_application_source.getText(),
                                    textfield_update_interval.getText(),
                                    interval.getValue().toString())
        );
        
        Button logout = new Button("Exit");
        grid.add(logout,1,6);
        logout.setOnAction(e -> System.exit(0));
       
        Scene app = new Scene(grid);
        
        return app;
    }
    
    private Scene noConfigScene(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text prompt_title = new Text("Warning!");
        prompt_title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(prompt_title, 0, 0, 2, 1);

        Label prompt_description = new Label("A configuration file for this client could not be found.");
        grid.add(prompt_description, 0, 1, 2, 1);

        Label prompt_question = new Label("Would you like to proceed with generating a new configuration file?");
        grid.add(prompt_question, 0, 2, 2, 1);

        Button accept_button = new Button("Proceed");
        accept_button.setOnAction(e -> primaryStage.setScene(configInputPrompt(primaryStage)));
        grid.add(accept_button, 0, 3);

        Button decline_button = new Button("Quit");
        decline_button.setOnAction(e -> System.exit(0));
        grid.add(decline_button, 1, 3);

        return new Scene(grid);
    }

    private Scene configInputPrompt(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label prompt_description = new Label("Please provide the url for the\nLogCollector server:");
        grid.add(prompt_description, 0, 0, 2, 1);

        TextField url_input = new TextField();
        grid.add(url_input, 0, 1, 2, 1);

        Button accept_button = new Button("Done");
        accept_button.setOnAction((ActionEvent e) -> {
            Configurations.generateClientPropertiesFile(url_input.getText());
            Configurations.readPropertiesFile();
            primaryStage.setScene(betaMenu(primaryStage));
        });
        grid.add(accept_button, 0, 2);

        Button decline_button = new Button("Quit");
        decline_button.setOnAction(e -> System.exit(0));
        grid.add(decline_button, 1, 2);

        return new Scene(grid);
    }

    private void loginCheck(String username, String password, Stage primaryStage) {
        
        this.username = username;
        this.password = DataManaging.hashString(password);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        if (this.username != null && this.username.length() >= 1 &&
            this.password != null && this.password.length() >= 1) {
            
            if (this.connections.authenticate(this.username, this.password)) {
                primaryStage.setScene(app(primaryStage));
            } else {
                if (this.connections.AUTHENTICATION_ERROR_MESSAGE != null) {
                    alert.setTitle("Invalid login");
                    alert.setContentText(this.connections.AUTHENTICATION_ERROR_MESSAGE);
                    alert.show();
                } else {
                    alert.setContentText("Couldn't connect to the URL provided\nin the configuration file.");
                    alert.show();
                    primaryStage.setScene(configInputPrompt(primaryStage));
                }
            }
        }
    }
    
    private void newApplication(Stage primaryStage,
                                String application_name,
                                String application_source_uri,
                                String update_interval,
                                String interval_option) {
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sending log");
        alert.setContentText("Sending log may take a few minutes.\nPress OK to proceed.");
        alert.showAndWait();
        
        DataManaging.newApplication(
                this.username,
                this.password,
                application_name,
                application_source_uri,
                update_interval,
                interval_option
        );
        
        betaAutomaticUpdates(primaryStage);
    }
    
    private void betaAutomaticUpdates(Stage primaryStage) {
        primaryStage.close();
        
        AutomaticUpdate automatic_update = new AutomaticUpdate();
        automatic_update.startAllUpdaters();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}