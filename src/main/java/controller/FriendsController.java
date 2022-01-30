package controller;

import business.FriendshipService;
import business.MessageService;
import business.ReportService;
import business.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.dto.FriendDTO;
import model.dto.UserDTO;
import utils.Constants;
import utils.config.ApplicationContext;
import utils.events.FriendshipServiceEvent;
import utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.StreamSupport;

public class FriendsController implements Observer<FriendshipServiceEvent> {

    private UserService userService;
    private FriendshipService friendshipService;
    private ApplicationContext applicationContext;
    private UserDTO loggedUser;
    private final ObservableList<FriendDTO> friendsModel = FXCollections.observableArrayList();

    @FXML
    TableView<FriendDTO> tableView;
    @FXML
    TableColumn<FriendDTO, String> tableNameColumn;
    @FXML
    TableColumn<FriendDTO, LocalDate> tableDateColumn;

    @FXML
    public void initialize(){
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("friend"));
        tableDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableDateColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<FriendDTO, LocalDate> call(TableColumn<FriendDTO, LocalDate> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.format(Constants.DISPLAY_DATE_FORMATTER));
                        }
                    }
                };
            }
        });
        tableView.setItems(friendsModel);
    }

    private void updateModel(){
        Iterable<FriendDTO> friendshipRequests = friendshipService.getFriendsOfUser(loggedUser.getUsername());
        List<FriendDTO> requestDTOList = StreamSupport.stream(friendshipRequests.spliterator(), false).toList();
        friendsModel.setAll(requestDTOList);
    }

    public void onBackButtonPressed() {
        Stage stage = (Stage) tableView.getScene().getWindow();
        applicationContext.getWindowController().setTitle("ToySocialNetwork");

        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/mainMenuView.fxml"));
            Parent layout = root.load();
            Scene mainMenu = new Scene(layout);

            MainMenuController mainMenuController = root.getController();
            mainMenuController.setLoggedUser(loggedUser);
            mainMenuController.setApplicationContext(applicationContext);

            stage.setScene(mainMenu);
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    public void onRemoveFriendButtonPressed() {
        FriendDTO friendDTO = tableView.getSelectionModel().getSelectedItem();
        if (friendDTO == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a friend first!");
            alert.showAndWait();
            return;
        }
        friendshipService.removeFriendship(loggedUser.getUsername(), friendDTO.getFriend().getUsername());
    }

    public void onAddFriendButtonPressed() {
        Stage primaryStage = applicationContext.getPrimaryStage();
        Stage stage = new Stage();
        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/addFriendView.fxml"));
            Parent layout = root.load();
            Scene addFriendView = new Scene(layout);

            AddFriendController addFriendController = root.getController();
            addFriendController.setLoggedUser(loggedUser);
            addFriendController.setApplicationContext(applicationContext);

            stage.setScene(addFriendView);
            stage.setTitle("Send friend requests");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.userService = applicationContext.getUserService();
        this.friendshipService = applicationContext.getFriendshipService();
        friendshipService.addObserver(this);
        updateModel();
    }

    public void setLoggedUser(UserDTO user) {
        loggedUser = user;
    }

    @Override
    public void update(FriendshipServiceEvent event) {
        updateModel();
    }
}
