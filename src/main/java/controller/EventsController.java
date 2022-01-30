package controller;

import business.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.dto.UserDTO;
import utils.config.ApplicationContext;
import utils.events.ParticipationEventInterface;
import utils.observer.Observer;

import java.io.IOException;

public class EventsController {
    private UserService userService;
    private FriendshipService friendshipService;
    private MessageService messageService;
    private EventService eventService;
    private EventParticipationService eventParticipationService;
    private UserDTO loggedUser;
    private ApplicationContext applicationContext;
    private Observer<ParticipationEventInterface> lastControllerUsed = null;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.userService = applicationContext.getUserService();
        this.friendshipService = applicationContext.getFriendshipService();
        this.messageService = applicationContext.getMessageService();
        this.eventService = applicationContext.getEventService();
        this.eventParticipationService = applicationContext.getEventParticipationService();
    }

    public void setLoggedUser(UserDTO loggedUser){
        this.loggedUser = loggedUser;
    }

    @FXML
    private BorderPane mainPane;

    @FXML
    public void onBackButtonClicked(){
        Stage stage = applicationContext.getPrimaryStage();
        try {
            if(lastControllerUsed != null)
                eventParticipationService.removeObserver(lastControllerUsed);

            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/mainMenuView.fxml"));
            Parent layout = root.load();
            Scene addFriendView = new Scene(layout);

            MainMenuController mainMenuController = root.getController();
            mainMenuController.setLoggedUser(loggedUser);
            mainMenuController.setApplicationContext(applicationContext);

            stage.setScene(addFriendView);
            applicationContext.getWindowController().setTitle("Main menu");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            stage.close();
        }
    }

    @FXML
    private void onNewEventButtonClicked(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../views/addEventView.fxml"));
            Node addEventPane = fxmlLoader.load();
            mainPane.setCenter(addEventPane);

            AddEventController addEventController = fxmlLoader.getController();
            addEventController.setLoggedUser(loggedUser);
            addEventController.setApplicationContext(applicationContext);
        }
        catch (IOException exception){
            exception.printStackTrace();
        }
    }

    @FXML
    private void onDiscoverButtonClicked(){
        try {
            if(lastControllerUsed != null)
                eventParticipationService.removeObserver(lastControllerUsed);

            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/discoverEventsView.fxml"));
            Node discoverEventPane = root.load();
            mainPane.setCenter(discoverEventPane);

            DiscoverEventsController discoverEventsController = root.getController();
            lastControllerUsed = discoverEventsController;

            discoverEventsController.setLoggedUser(loggedUser);
            discoverEventsController.setApplicationContext(applicationContext);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onMyEventsButtonClicked(){
        try {
            if(lastControllerUsed != null)
                eventParticipationService.removeObserver(lastControllerUsed);

            FXMLLoader root = new FXMLLoader();
            root.setLocation(getClass().getResource("../views/myEventsView.fxml"));
            Node myEventsPane = root.load();
            mainPane.setCenter(myEventsPane);

            MyEventsController myEventsController = root.getController();
            lastControllerUsed = myEventsController;
            myEventsController.setLoggedUser(loggedUser);
            myEventsController.setApplicationContext(applicationContext);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
