package logaggregatorclient;

import java.io.File;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
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
    
    public Configurations.Application[] applications;
    private final AutomaticUpdate automatic_update = new AutomaticUpdate();
    
    private final Button start_updates_button = new Button("Start updates");
    private final Button stop_updates_button = new Button("Stop updates");
    
    public static final TableView<Configurations.Application> application_table = new TableView();
    
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
        this.applications = Configurations.getApplicationConfigurations();
        
        TableColumn<Configurations.Application, String> application_name_column = new TableColumn<>("Application name");
        application_name_column.setMinWidth(150);
        application_name_column.setCellValueFactory(cell_data -> {
            return new ReadOnlyStringWrapper(cell_data.getValue().application_name);
        });
        
        TableColumn<Configurations.Application, String> update_interval_column = new TableColumn<>("Update interval");
        update_interval_column.setMinWidth(150);
        update_interval_column.setCellValueFactory(cell_data -> {
            return new ReadOnlyStringWrapper(cell_data.getValue().update_interval);
        });
        
        TableColumn<Configurations.Application, String> local_source_column = new TableColumn<>("Local source");
        local_source_column.setMinWidth(500);
        local_source_column.setCellValueFactory(cell_data -> {
            return new ReadOnlyStringWrapper(cell_data.getValue().log_uri);
        });
        
        application_table.getColumns().addAll(application_name_column, update_interval_column, local_source_column);
        
        this.start_updates_button.setDisable(false);
        this.start_updates_button.setOnAction((ActionEvent e) -> {
            automatic_update.loadApplications();
            automatic_update.startAllUpdaters();
            
            if (stop_updates_button.isDisable()) stop_updates_button.setDisable(false);
            if (!start_updates_button.isDisable()) start_updates_button.setDisable(true);
        });
        
        this.stop_updates_button.setDisable(true);
        this.stop_updates_button.setOnAction((ActionEvent e) -> {
            automatic_update.stopAllUpdaters();
            
            if (start_updates_button.isDisable()) start_updates_button.setDisable(false);
            if (!stop_updates_button.isDisable()) stop_updates_button.setDisable(true);
        });
        
        if (Configurations.client_configurations_found) {
            for (Configurations.Application application : this.applications) {
                application_table.getItems().add(application);
            }
            
            primaryStage.setScene(startupMenu(primaryStage));
        } else {
            primaryStage.setScene(noConfigScene(primaryStage));
        }
        
        primaryStage.show();
    }
    
    private Scene startupMenu(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text title = new Text("Application logs on this device.");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(title, 0, 0, 5, 1);
        
        grid.add(application_table, 0, 1, 5, 1);
        
        HBox start_updates_button_hbox = new HBox(10);
        start_updates_button_hbox.setAlignment(Pos.BOTTOM_LEFT);
        start_updates_button_hbox.getChildren().add(this.start_updates_button);
        grid.add(start_updates_button_hbox, 0, 2, 1, 1);
        
        HBox stop_updates_button_hbox = new HBox(10);
        stop_updates_button_hbox.setAlignment(Pos.BOTTOM_LEFT);
        stop_updates_button_hbox.getChildren().add(this.stop_updates_button);
        grid.add(stop_updates_button_hbox, 1, 2, 1, 1);
        
        Button new_application_button = new Button("New application");
        new_application_button.setOnAction(e -> primaryStage.setScene(loginScene(primaryStage)));
        
        HBox new_application_button_hbox = new HBox(10);
        new_application_button_hbox.setAlignment(Pos.BOTTOM_RIGHT);
        new_application_button_hbox.getChildren().add(new_application_button);
        grid.add(new_application_button_hbox, 4, 2, 1, 1);
        
        Button change_url_botton = new Button("Change server URL");
        change_url_botton.setOnAction(e -> primaryStage.setScene(configInputPromptScene(primaryStage)));
        
        HBox change_url_botton_hbox = new HBox(10);
        change_url_botton_hbox.setAlignment(Pos.BOTTOM_RIGHT);
        change_url_botton_hbox.getChildren().add(change_url_botton);
        grid.add(change_url_botton_hbox, 3, 2, 1, 1);
        
        return new Scene(grid, 860, 400);
    }
    
    private Scene loginScene(Stage primaryStage){
        connections.checkConfigurations();
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text login_title = new Text("Login");
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
        login_button.setOnAction(e ->
                this.loginCheck(username_textfield.getText(),
                                password_passwordfield.getText(),
                                primaryStage)
        );
        
        login_button_hbox.setAlignment(Pos.BOTTOM_RIGHT);
        login_button_hbox.getChildren().add(login_button);
        grid.add(login_button_hbox, 1, 4);
        
        Button return_button = new Button("Return");
        return_button.setOnAction(e -> primaryStage.setScene(startupMenu(primaryStage)));
        
        HBox return_button_hbox = new HBox(10);
        return_button_hbox.setAlignment(Pos.BOTTOM_LEFT);
        return_button_hbox.getChildren().add(return_button);
        grid.add(return_button_hbox, 0, 4);
        
        Scene login = new Scene(grid, 350, 270);
        
        return login;
    }
    
    private Scene newApplicationScene(Stage primaryStage) {
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
                  textfield_application_source.setText(logfile.getAbsolutePath());
                }
                
            }
        });
        
        Button exportbtn = new Button("Export");
        grid.add(exportbtn, 0, 6);
        exportbtn.setOnAction((ActionEvent e) -> {
            String tmpName, tmpSource, tmpInterval;
            if ((tmpName = textfield_application_name.getText()) != null &&
                 tmpName.length() >= 1 && (
                 tmpSource = textfield_application_source.getText()) != null &&
                 tmpSource.length() >= 1 && (
                 tmpInterval = textfield_update_interval.getText()) != null &&
                 tmpInterval.length() >= 1
            ) {
                
                exportbtn.setDisable(true);
                newApplication(tmpName,
                               tmpSource,
                               tmpInterval,
                               interval.getValue().toString());
                applications = Configurations.getApplicationConfigurations();
                primaryStage.setScene(startupMenu(primaryStage));
            }
        });
        
        Button logout = new Button("Return");
        grid.add(logout,1,6);
        logout.setOnAction(e -> primaryStage.setScene(startupMenu(primaryStage)));
        
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
        accept_button.setOnAction(e -> primaryStage.setScene(configInputPromptScene(primaryStage)));
        grid.add(accept_button, 0, 3);
        
        Button decline_button = new Button("Exit");
        decline_button.setOnAction(e -> System.exit(0));
        grid.add(decline_button, 1, 3);

        return new Scene(grid);
    }

    private Scene configInputPromptScene(Stage primaryStage) {
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
            Configurations.changeServerURL(url_input.getText());
            Configurations.readPropertiesFile();
            applications = Configurations.getApplicationConfigurations();
            primaryStage.setScene(startupMenu(primaryStage));
        });
        grid.add(accept_button, 0, 2);
        
        Button decline_button = new Button("Cancel");
        decline_button.setOnAction(e -> primaryStage.setScene(loginScene(primaryStage)));
        grid.add(decline_button, 1, 2);

        return new Scene(grid, 350, 270);
    }

    private void loginCheck(String username, String password, Stage primaryStage) {
        
        this.username = username;
        this.password = DataManaging.hashString(password);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        
        if (this.username != null && this.username.length() >= 1 &&
            this.password != null && this.password.length() >= 1) {
            
            if (this.connections.authenticate(this.username, this.password)) {
                primaryStage.setScene(newApplicationScene(primaryStage));
            } else {
                if (this.connections.AUTHENTICATION_ERROR_MESSAGE != null) {
                    alert.setTitle("Invalid login");
                    alert.setContentText(this.connections.AUTHENTICATION_ERROR_MESSAGE);
                    alert.show();
                } else {
                    alert.setContentText("Couldn't connect to the URL provided\nin the configuration file.");
                    alert.show();
                    primaryStage.setScene(configInputPromptScene(primaryStage));
                }
            }
        }
    }
    
    private void newApplication(String application_name,
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
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}