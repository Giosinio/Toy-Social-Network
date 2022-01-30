package controller;

import business.FriendshipService;
import business.UserService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.User;
import model.dto.FriendDTO;
import model.dto.FriendshipDTO;
import model.dto.UserDTO;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import utils.Constants;
import utils.config.ApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AddFriendController {

    private UserService userService;
    private FriendshipService friendshipService;
    private ApplicationContext applicationContext;
    private UserDTO loggedUser;
    private ObservableList<UserDTO> users = FXCollections.observableArrayList();

    @FXML
    TextField nameField;

    @FXML
    TableView<UserDTO> tableView;
    @FXML
    TableColumn<UserDTO, String> tableFirstNameColumn;
    @FXML
    TableColumn<UserDTO, LocalDate> tableLastNameColumn;

    public void setLoggedUser(UserDTO user) {
        loggedUser = user;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.userService = applicationContext.getUserService();
        this.friendshipService = applicationContext.getFriendshipService();
        updateModel();
    }

    public void updateModel() {
        String searchText = nameField.getText();
        List<UserDTO> userDTOList = friendshipService.findRecommendedFriends(loggedUser.getUsername(), searchText);
        users.setAll(userDTOList);
    }

    @FXML
    public void initialize() {
        tableView.setItems(users);
        tableFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        StringConverter<UserDTO> userConverter = new StringConverter<UserDTO>() {
            @Override
            public String toString(UserDTO object) {
                return object.getFullName();
            }

            @Override
            public UserDTO fromString(String string) {
                return userService.findUserByName(string);
            }
        };

        TextFields.bindAutoCompletion(nameField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<UserDTO>>() {
            @Override
            public Collection<UserDTO> call(AutoCompletionBinding.ISuggestionRequest param) {
                return friendshipService.findRecommendedFriends(loggedUser.getUsername(), param.getUserText());
            }
        }, userConverter);

        nameField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateModel();
            }
        });
    }

    public void onSendButtonPressed(ActionEvent clickedEvent) {
        UserDTO selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an user first!");
            alert.showAndWait();
            return;
        }
        try {
            friendshipService.sendFriendshipRequest(loggedUser.getUsername(), selectedUser.getUsername());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Friend request sent successfully!");
            alert.showAndWait();
        } catch (RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }

    public void onCancelButtonPressed(ActionEvent clickedEvent) {
        Stage stage = (Stage) tableView.getScene().getWindow();
        stage.close();
    }
}
