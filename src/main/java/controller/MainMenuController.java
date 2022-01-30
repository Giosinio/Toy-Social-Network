package controller;

import business.*;
import business.FriendshipService;
import business.MessageService;
import business.ReportService;
import business.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.dto.UserDTO;
import utils.config.ApplicationContext;

import java.io.IOException;

public class MainMenuController {

    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private EventService eventService;
    private EventParticipationService eventParticipationService;
    private ReportService reportService;
    private UserDTO loggedUser;
    private ApplicationContext applicationContext;
    private ToggleGroup toggleGroup;

    @FXML
    private Label welcomeLabel;

    @FXML
    private AnchorPane appMenuContent;

    @FXML
    private ToggleButton pageButton, friendsButton, friendRequestsButton, messagesButton, eventsButton, reportsButton;
    @FXML
    private Button logoutButton;

    @FXML
    void initialize() throws IOException {
        toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().setAll(pageButton, friendsButton, friendRequestsButton, messagesButton, eventsButton, reportsButton);
        toggleGroup.selectToggle(pageButton);
    }

    public void setContents(Node contents) {
        appMenuContent.getChildren().setAll(contents);
        AnchorPane.setTopAnchor(contents, 5d);
        AnchorPane.setRightAnchor(contents, 5d);
        AnchorPane.setBottomAnchor(contents, 5d);
        AnchorPane.setLeftAnchor(contents, 5d);
    }

    @FXML
    protected void onPageButtonClicked() {
        Stage stage = applicationContext.getPrimaryStage();
        applicationContext.getWindowController().setTitle("Home page");

        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/pageView.fxml"));
            Parent layout = root.load();

            PageController pageController = root.getController();
            pageController.setLoggedUser(loggedUser);
            pageController.setApplicationContext(applicationContext);
            pageController.setMainMenuController(this);

            setContents(layout);
            toggleGroup.selectToggle(pageButton);
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    @FXML
    protected void openFriendshipRequestMenu() {
        Stage stage = applicationContext.getPrimaryStage();
        applicationContext.getWindowController().setTitle("Friendship requests");
        try{
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/friendshipRequestView.fxml"));
            Parent layout = root.load();

            FriendshipRequestController frController = root.getController();
            frController.setLoggedUser(loggedUser);
            frController.setApplicationContext(applicationContext);

            setContents(layout);
            toggleGroup.selectToggle(friendRequestsButton);
        }catch (IOException exception){
            exception.printStackTrace();
            stage.close();
        }
    }

    @FXML
    private void onNotificationsButtonClicked(){
        Stage newStage = new Stage();
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../views/notificationView.fxml"));
            Parent layout = fxmlLoader.load();
            Scene scene = new Scene(layout);

            NotificationController notificationController = fxmlLoader.getController();
            notificationController.setLoggedUser(loggedUser);
            notificationController.setNotificationService(applicationContext.getNotificationService());

            newStage.setTitle("Notifications");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openEventsMenu() {
        Stage stage = applicationContext.getPrimaryStage();
        applicationContext.getWindowController().setTitle("Events");
        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/eventsView.fxml"));
            Parent layout = root.load();

            EventsController eventsController = root.getController();
            eventsController.setLoggedUser(loggedUser);
            eventsController.setApplicationContext(applicationContext);

            setContents(layout);
            toggleGroup.selectToggle(eventsButton);
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    public void setLoggedUser(UserDTO user) {
        loggedUser = user;
        welcomeLabel.setText("Welcome, " + user.getFirstName() + "!");
    }

    public void onFriendsButtonClicked() {
        Stage stage = applicationContext.getPrimaryStage();
        applicationContext.getWindowController().setTitle("Friends");

        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/friendsView.fxml"));
            Parent layout = root.load();

            FriendsController friendsController = root.getController();
            friendsController.setLoggedUser(loggedUser);
            friendsController.setApplicationContext(applicationContext);

            setContents(layout);
            toggleGroup.selectToggle(friendsButton);
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    public void openReportsMenu() {
        Stage stage = applicationContext.getPrimaryStage();
        applicationContext.getWindowController().setTitle("Reports");

        try {
            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/reportsView.fxml"));
            Parent layout = root.load();

            ReportsController reportsController = root.getController();
            reportsController.setLoggedUser(loggedUser);
            reportsController.setApplicationContext(applicationContext);

            setContents(layout);
            toggleGroup.selectToggle(reportsButton);
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    @FXML
    private void onLogoutButtonClicked(){
        Stage primaryStage = applicationContext.getPrimaryStage();

        try {
            FXMLLoader newWindowFXMLLoader = new FXMLLoader(getClass().getResource("../views/loginWindow.fxml"));
            Parent newWindowLayout = newWindowFXMLLoader.load();
            WindowController newWindowController = newWindowFXMLLoader.getController();
            primaryStage.getScene().setRoot(newWindowLayout);
            primaryStage.getScene().setFill(Color.TRANSPARENT);

            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/loginView.fxml"));
            Parent layout = root.load();

            LoginController loginController = root.getController();
            loginController.setApplicationContext(applicationContext);

            applicationContext.setWindowController(newWindowController);
            newWindowController.setTitle("Login");
            newWindowController.setContents(layout, 50d, null, 50d, 50d);
            newWindowController.initWindow();
        } catch (IOException e) {
            e.printStackTrace();
            primaryStage.close();
        }
    }

    @FXML
    private void onAboutButtonClicked(){
        Stage primaryStage = applicationContext.getPrimaryStage();
        Stage newStage = new Stage(StageStyle.TRANSPARENT);
        newStage.initOwner(primaryStage);
        newStage.initModality(Modality.APPLICATION_MODAL);

        try {
            FXMLLoader newWindowFXMLLoader = new FXMLLoader(getClass().getResource("../views/window.fxml"));
            Parent newWindowLayout = newWindowFXMLLoader.load();
            WindowController newWindowController = newWindowFXMLLoader.getController();
            Scene newScene = new Scene(newWindowLayout);
            newStage.setScene(newScene);
            newStage.getScene().setFill(Color.TRANSPARENT);

            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/aboutView.fxml"));
            Parent layout = root.load();

            AboutController aboutController = root.getController();

            newWindowController.setTitle("About");
            newWindowController.setContents(layout);
            newWindowController.initWindow();
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            primaryStage.close();
        }
    }

    @FXML
    public void openMessagesMenu(){
        Stage primaryStage = applicationContext.getPrimaryStage();
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../views/messagesView.fxml"));
            Parent layout = loader.load();
            applicationContext.getWindowController().setTitle("Messages");
            primaryStage.centerOnScreen();

            MessagesController messagesController = loader.getController();
            messagesController.setLoggedUser(loggedUser);
            messagesController.setApplicationContext(applicationContext);

            applicationContext.getWindowController().setContents(layout);
        } catch (IOException e){
            e.printStackTrace();
            primaryStage.close();
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws IOException {
        this.applicationContext = applicationContext;

        FXMLLoader subSceneFXMLLoader = new FXMLLoader();
        subSceneFXMLLoader.setLocation(getClass().getResource("../views/pageView.fxml"));
        Parent subSceneLayout = subSceneFXMLLoader.load();
        PageController pageController = subSceneFXMLLoader.getController();
        pageController.setLoggedUser(loggedUser);
        pageController.setApplicationContext(applicationContext);
        pageController.setMainMenuController(this);

        setContents(subSceneLayout);

        this.userService = applicationContext.getUserService();
        this.friendshipService = applicationContext.getFriendshipService();
        this.messageService = applicationContext.getMessageService();
        this.eventService = applicationContext.getEventService();
        this.eventParticipationService = applicationContext.getEventParticipationService();
        this.reportService = applicationContext.getReportService();
    }

}
