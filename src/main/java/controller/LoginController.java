package controller;

import business.*;
import business.FriendshipService;
import business.MessageService;
import business.ReportService;
import business.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.dto.UserDTO;
import utils.config.ApplicationContext;

import java.io.IOException;

public class LoginController {

    private UserService userService;
    private UserDTO loggedUser;
    private ApplicationContext applicationContext;

    @FXML
    private Text textResponse;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleSubmitButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            UserDTO loggedUser = userService.findUser(username);
            if (loggedUser == null) {
                textResponse.setText("The given username wasn't found!");
            } else if (!userService.checkPassword(username, password)) {
                textResponse.setText("Incorrect password!");
            } else {
                textResponse.setText("Login successful!");
                this.loggedUser = loggedUser;
                //update Notifications status(for 'disable' field)
                applicationContext.getNotificationService().changeDisableStatusForNotificationsInInterval(loggedUser.getUsername());
                openMainMenu();
            }
        } catch (RuntimeException e) {
            textResponse.setText(e.getMessage());
            throw e;
        }
    }

    private void openMainMenu() {
        Stage stage = applicationContext.getPrimaryStage();

        try {
            FXMLLoader newWindowFXMLLoader = new FXMLLoader(getClass().getResource("../views/window.fxml"));
            Parent newWindowLayout = newWindowFXMLLoader.load();
            WindowController newWindowController = newWindowFXMLLoader.getController();
            stage.getScene().setRoot(newWindowLayout);
            stage.getScene().setFill(Color.TRANSPARENT);

            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/mainMenuView.fxml"));
            Parent layout = root.load();

            MainMenuController mainMenuController = root.getController();
            mainMenuController.setLoggedUser(loggedUser);
            mainMenuController.setApplicationContext(applicationContext);

            applicationContext.setWindowController(newWindowController);
            newWindowController.setTitle("Home page");
            newWindowController.setContents(layout);
            newWindowController.initWindow();
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    @FXML
    private void onRegisterButtonClicked(){
        Stage stage = applicationContext.getPrimaryStage();
        applicationContext.getWindowController().setTitle("Register");

        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/registerView.fxml"));
            Parent layout = root.load();

            RegisterController registerController = root.getController();
            registerController.setApplicationContext(applicationContext);

            applicationContext.getWindowController().setContents(layout, 50d, null, 50d, 50d);
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.userService = applicationContext.getUserService();
    }
}
