package controller;

import business.FriendshipService;
import business.MessageService;
import business.ReportService;
import business.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import model.dto.FriendshipRequestDTO;
import model.dto.UserDTO;
import utils.config.ApplicationContext;
import utils.events.FriendshipServiceEvent;
import utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;


public class FriendshipRequestController implements Observer<FriendshipServiceEvent> {
    public Button acceptButton;
    public Button deleteButton;
    private ToggleGroup frToggleGroup;
    private UserService userService;
    private FriendshipService friendshipService;
    private UserDTO loggedUser;
    private ApplicationContext applicationContext;
    private final ObservableList<FriendshipRequestDTO> model = FXCollections.observableArrayList();

    @FXML
    Alert noFrSelectedAlert;

    @FXML
    Button backButton;

    @FXML
    Button cancelButton;

    @FXML
    TableView<FriendshipRequestDTO> frTableView;

    @FXML
    TableColumn<FriendshipRequestDTO, String> fromTableColumn;

    @FXML
    TableColumn<FriendshipRequestDTO, String> dateTableColumn;

    @FXML
    TableColumn<FriendshipRequestDTO, String> statusTableColumn;

    @FXML
    RadioButton pendingRadioButton;

    @FXML
    RadioButton sentRadioButton;

    private void setPendingFRModel() {
        fromTableColumn.setCellValueFactory(new PropertyValueFactory("fromUserDTO"));
        fromTableColumn.setText("From");

        acceptButton.setVisible(true);
        acceptButton.setManaged(true);
        deleteButton.setVisible(true);
        deleteButton.setManaged(true);
        cancelButton.setVisible(false);
        cancelButton.setManaged(false);
    }

    private void setSentFRModel() {
        fromTableColumn.setCellValueFactory(new PropertyValueFactory("toUserDTO"));
        fromTableColumn.setText("To");

        acceptButton.setVisible(false);
        acceptButton.setManaged(false);
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
        cancelButton.setVisible(true);
        cancelButton.setManaged(true);
    }

    @FXML
    public void initialize(){
        noFrSelectedAlert = new Alert(Alert.AlertType.INFORMATION, "You didn't select any request!");

        dateTableColumn.setCellValueFactory(new PropertyValueFactory("created"));
        statusTableColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<FriendshipRequestDTO, String> call(TableColumn<FriendshipRequestDTO, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText("PENDING");
                        }
                    }
                };
            }
        });

        frToggleGroup = new ToggleGroup();
        pendingRadioButton.setToggleGroup(frToggleGroup);
        sentRadioButton.setToggleGroup(frToggleGroup);
        frToggleGroup.selectToggle(pendingRadioButton);
        frToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == pendingRadioButton) {
                    setPendingFRModel();
                } else if (newValue == sentRadioButton) {
                    setSentFRModel();
                }
                initModel();
            }
        });

        cancelButton.setManaged(false);

        setPendingFRModel();
        frTableView.setItems(model);
    }

    private void initModel() {
        Iterable<FriendshipRequestDTO> friendshipRequests;
        if (frToggleGroup.getSelectedToggle() == pendingRadioButton) {
            friendshipRequests = friendshipService.getFriendshipRequestsToUser(loggedUser.getUsername());
        } else {
            friendshipRequests = friendshipService.getFriendshipRequestsFromUser(loggedUser.getUsername());
        }
        List<FriendshipRequestDTO> requestDTOList = StreamSupport.stream(friendshipRequests.spliterator(), false)
                .toList();
        model.setAll(requestDTOList);
    }

    @FXML
    protected void handleAcceptFriendshipRequest(){
        FriendshipRequestDTO friendshipRequestDTO = frTableView.getSelectionModel().getSelectedItem();
        if(friendshipRequestDTO == null){
            noFrSelectedAlert.show();
        }
        else{
            String fromUsername = friendshipRequestDTO.getFromUserDTO().getUsername();
            friendshipService.acceptFriendshipRequest(loggedUser.getUsername(), fromUsername);
        }
    }

    @FXML
    protected void handleDeleteFriendshipRequest(){
        FriendshipRequestDTO friendshipRequestDTO = frTableView.getSelectionModel().getSelectedItem();
        if(friendshipRequestDTO == null){
            noFrSelectedAlert.show();
        }
        else{
            String fromUsername = friendshipRequestDTO.getFromUserDTO().getUsername();
            friendshipService.rejectFriendshipRequest(loggedUser.getUsername(), fromUsername);
        }
    }

    @FXML
    protected void handleCancelFriendshipRequest() {
        FriendshipRequestDTO friendshipRequestDTO = frTableView.getSelectionModel().getSelectedItem();
        if(friendshipRequestDTO == null){
            noFrSelectedAlert.show();
        }
        else{
            String toUsername = friendshipRequestDTO.getToUserDTO().getUsername();
            friendshipService.cancelFriendshipRequest(loggedUser.getUsername(), toUsername);
        }
    }

    @FXML
    protected void onAddFriendButtonClicked() {
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
        this.friendshipService.addObserver(this);
        initModel();
    }

    public void setLoggedUser(UserDTO userDTO){
        this.loggedUser = userDTO;
    }

    @Override
    public void update(FriendshipServiceEvent event) {
        initModel();
    }
}
