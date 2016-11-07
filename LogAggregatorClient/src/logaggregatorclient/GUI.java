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

public class GUI extends Application {
    
    private String username = null;
    private String password = null;

    private final String COMBOBOX_HOURS = "Hours";
    private final String COMBOBOX_MINUTES = "Minutes";
    private final String COMBOBOX_SECONDS = "Seconds";
    
    private final Connections connections = new Connections();
    private final LogHandler logs = new LogHandler();
    private final DataManaging data_managing = new DataManaging();
    
    @Override
    public void start(Stage primaryStage) {
        /*
        This method contains the UI that will initialize when the 
        application starts.
        
        Maybe a login window or something similar could be created here.
        */
        
        Configurations.readPropertiesFile();
        
        primaryStage.setScene(betaMenu(primaryStage));
//        primaryStage.setTitle("Login");
        primaryStage.show();
    }
    
    private Scene betaMenu(Stage primaryStage){
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
        interval.getItems().addAll(this.COMBOBOX_HOURS,
                                   this.COMBOBOX_MINUTES,
                                   this.COMBOBOX_SECONDS);
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
    
    private void loginCheck(String username, String password, Stage primaryStage) {
        
        this.username = username;
        this.password = DataManaging.hashString(password);
        Alert alert =new Alert(Alert.AlertType.WARNING);
        
        if (this.username != null && this.username.length() >= 1 &&
            this.password != null && this.password.length() >= 1) {
            
            if (this.connections.authenticate(this.username, this.password)) {
                primaryStage.setScene(app(primaryStage));
            } else {
                alert.setTitle("Login fail");
                alert.setContentText(this.connections.AUTHENTICATION_ERROR_MESSAGE);
                alert.show();
            }
        }
    }
    
    private void newApplication(Stage primaryStage,
                                String application_name,
                                String application_source_uri,
                                String update_interval,
                                String interval_option) {
        
        String update_interval_calculated = update_interval;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sending log");
        alert.setContentText("Sending log may take a few minutes.\nPress OK to proceed.");
        alert.showAndWait();
        
        if (interval_option.equals(this.COMBOBOX_HOURS)) {
            update_interval_calculated = Integer.toString(Integer.parseInt(update_interval) * 60 * 60);
        } else if (interval_option.equals(this.COMBOBOX_MINUTES)) {
            update_interval_calculated = Integer.toString(Integer.parseInt(update_interval) * 60);
        }
        
        String api_key = this.connections.new_application(
                this.username,
                this.password,
                application_name,
                update_interval_calculated);
        
        this.logs.read(application_source_uri, null, null, null);
        
        this.data_managing.generatePropertiesFile(
                application_source_uri,
                api_key,
                update_interval_calculated,
                this.logs.last_read_line_one,
                this.logs.last_read_line_two,
                this.logs.last_read_line_three);
        
        this.connections.send_logs(api_key, this.logs.zip_path);
        
        betaAutomaticUpdates(primaryStage);
    }
    
    private void betaAutomaticUpdates(Stage primaryStage) {
        primaryStage.close();
        
        AutomaticUpdate automatic_update = new AutomaticUpdate();
        automatic_update.start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}