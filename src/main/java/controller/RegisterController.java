package controller;

import business.FriendshipService;
import business.MessageService;
import business.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import model.dto.UserDTO;
import utils.config.ApplicationContext;

import java.io.IOException;

public class RegisterController {
    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.userService = applicationContext.getUserService();
        this.friendshipService = applicationContext.getFriendshipService();
        this.messageService = applicationContext.getMessageService();
    }

    @FXML
    private TextField emailField, passwordField, firstNameField, lastNameField;

    @FXML
    private Text textResponse;

    private void getToLoginMenu(){
        Stage stage = applicationContext.getPrimaryStage();
        applicationContext.getWindowController().setTitle("Login");

        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/loginView.fxml"));
            Parent layout = root.load();

            LoginController loginController = root.getController();
            loginController.setApplicationContext(applicationContext);

            applicationContext.getWindowController().setContents(layout, 50d, null, 50d, 50d);
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    @FXML
    private void onCreateAccountButtonClicked(){
        String email = emailField.getText();
        String password = passwordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        try{
            UserDTO result = userService.addUser(email, password, firstName, lastName);
            if(result == null){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Account created successfully!");
                alert.show();
                getToLoginMenu();
            }
            else{
                textResponse.setText("The email address corresponds to another account!");
            }
        }
        catch (RuntimeException exception){
            textResponse.setText(exception.getMessage());
        }
    }

    @FXML
    private void onBackButtonPushed(){
        getToLoginMenu();
    }
}
