package controller;

import business.EventService;
import business.FriendshipService;
import business.PageService;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import model.dto.*;
import utils.Constants;
import utils.config.ApplicationContext;

import java.io.IOException;

public class PageController {

    private UserDTO loggedUser;
    private PageService pageService;
    private FriendshipService friendshipService;
    private ApplicationContext applicationContext;
    private MainMenuController mainMenuController;
    private PageDTO pageDTO;
    private long newFriendRequestsCount;

    @FXML
    Label userNameLabel, userIDLabel, lastLoginDateLabel;
    @FXML
    Label friendsLabel, friendRequestsLabel, messagesLabel, eventsLabel;
    @FXML
    Pane friendCards, friendRequestCards, messageCards, eventCards;
    @FXML
    ScrollPane notificationArea;
    @FXML
    ToggleButton notificationToggle;

    private class FriendCard {

        private Pane root;
        private FriendDTO friendDTO;

        FriendCard(FriendDTO friendDTO) {
            this.friendDTO = friendDTO;

            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../views/pageFriendCard.fxml"));
                Pane layout = fxmlLoader.load();

                Label friendNameLabel = (Label) layout.lookup("#friendNameLabel");
                friendNameLabel.setText(friendDTO.getFriend().getFullName());

                root = layout;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Pane getRoot() {
            return root;
        }

        public FriendDTO getFriendDTO() {
            return friendDTO;
        }
    }

    private class FriendRequestCard {

        private Pane root;
        private FriendshipRequestDTO friendshipRequestDTO;

        FriendRequestCard(FriendshipRequestDTO friendshipRequestDTO) {
            this.friendshipRequestDTO = friendshipRequestDTO;

            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../views/pageFriendRequestCard.fxml"));
                Pane layout = fxmlLoader.load();

                root = layout;

                Label friendRequestName = (Label) layout.lookup("#friendRequestName");
                friendRequestName.setText(friendshipRequestDTO.getFromUserDTO().getFullName());
                Label friendRequestDate = (Label) layout.lookup("#friendRequestDate");
                friendRequestDate.setText(friendshipRequestDTO.getCreated().format(Constants.DISPLAY_DATE_FORMATTER));

                Button friendRequestAccept = (Button) layout.lookup("#friendRequestAccept");
                friendRequestAccept.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        friendshipService.acceptFriendshipRequest(loggedUser.getUsername(),
                                friendshipRequestDTO.getFromUserDTO().getUsername());
                        friendRequestCards.getChildren().remove(root);
                        if (!(friendshipRequestDTO.getCreated().isBefore(pageDTO.getUserDTO().getLastLoginAt()))) {
                            newFriendRequestsCount--;
                            friendRequestsLabel.setText("Friend requests (" + newFriendRequestsCount + " new)");
                        }
                    }
                });

                Button friendRequestReject = (Button) layout.lookup("#friendRequestReject");
                friendRequestReject.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        friendshipService.rejectFriendshipRequest(loggedUser.getUsername(),
                                friendshipRequestDTO.getFromUserDTO().getUsername());
                        friendRequestCards.getChildren().remove(root);
                        if (!(friendshipRequestDTO.getCreated().isBefore(pageDTO.getUserDTO().getLastLoginAt()))) {
                            newFriendRequestsCount--;
                            friendRequestsLabel.setText("Friend requests (" + newFriendRequestsCount + " new)");
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Pane getRoot() {
            return root;
        }

        public FriendshipRequestDTO getFriendshipRequestDTO() {
            return friendshipRequestDTO;
        }
    }

    private class MessageCard {

        private Pane root;
        private MessageDTO messageDTO;

        MessageCard(MessageDTO messageDTO) {
            this.messageDTO = messageDTO;

            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../views/pageMessageCard.fxml"));
                Pane layout = fxmlLoader.load();

                Label messageDate = (Label) layout.lookup("#messageDate");
                messageDate.setText(messageDTO.getSendDate().format(Constants.DISPLAY_DATE_FORMATTER));
                Label messageFrom = (Label) layout.lookup("#messageFrom");
                messageFrom.setText(messageDTO.getFrom().getFullName());
                Label messageTo = (Label) layout.lookup("#messageTo");
                String to = messageDTO.getTo().get(0).getFullName();
                for (int i = 1; i < messageDTO.getTo().size(); i++) {
                    to += ", " + messageDTO.getTo().get(i).getFullName();
                }
                messageTo.setText(to);
                Label messageSubject = (Label) layout.lookup("#messageSubject");
                messageSubject.setText(messageDTO.getSubject());
                ScrollPane messageScrollPane = (ScrollPane) layout.getChildren().get(1);
                Text messageText = (Text) messageScrollPane.getContent().lookup("#message-text");
                String message = messageDTO.getMessage();
                if (messageDTO.getRepliesTo() != null) {
                    MessageDTO reply = messageDTO.getRepliesTo();
                    message += "\n\n--REPLIES TO--\n\n";
                    message += "\nSent: " + reply.getSendDate().format(Constants.DISPLAY_DATE_FORMATTER);
                    message += "\nFrom: " + reply.getFrom().getFullName();
                    message += "\nTo: " + reply.getTo().get(0).getFullName();
                    for (int i = 1; i < reply.getTo().size(); i++) {
                        message += ", " + reply.getTo().get(i).getFullName();
                    }
                    message += "\nSubject: " + reply.getSubject();
                    message += "\n" + reply.getMessage();
                }
                messageText.setText(message);

                root = layout;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Pane getRoot() {
            return root;
        }

        public MessageDTO getMessageDTO() {
            return messageDTO;
        }
    }

    private class EventCard {

        private Pane root;
        private EventParticipationDTO eventParticipationDTO;

        EventCard(EventParticipationDTO eventParticipationDTO) {
            this.eventParticipationDTO = eventParticipationDTO;

            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("../views/pageEventCard.fxml"));
                Pane layout = fxmlLoader.load();

                Label eventName = (Label) layout.lookup("#eventName");
                eventName.setText(eventParticipationDTO.getEventDTO().getEventName());
                Label eventParticipants = (Label) layout.lookup("#eventParticipants");
                eventParticipants.setText(String.valueOf(eventParticipationDTO.getEventDTO().getEventParticipants()));
                Label eventStartDate = (Label) layout.lookup("#eventStartDate");
                eventStartDate.setText(eventParticipationDTO.getEventDTO().getStartDate().format(Constants.DISPLAY_DATE_FORMATTER));
                Label eventEndDate = (Label) layout.lookup("#eventEndDate");
                eventEndDate.setText(eventParticipationDTO.getEventDTO().getEndDate().format(Constants.DISPLAY_DATE_FORMATTER));
                Label eventLocation = (Label) layout.lookup("#eventLocation");
                eventLocation.setText(eventParticipationDTO.getEventDTO().getLocation());
                Label eventOrganizer = (Label) layout.lookup("#eventOrganizer");
                eventOrganizer.setText(eventParticipationDTO.getEventDTO().getOrganiser());
                Label eventCategory = (Label) layout.lookup("#eventCategory");
                eventCategory.setText(eventParticipationDTO.getEventDTO().getCategory());
                ScrollPane eventScrollPane = (ScrollPane) layout.getChildren().get(1);
                Text eventText = (Text) eventScrollPane.getContent().lookup("#eventDescription");
                eventText.setText(eventParticipationDTO.getEventDTO().getDescription());

                root = layout;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Pane getRoot() {
            return root;
        }

        public EventParticipationDTO getEventParticipationDTO() {
            return eventParticipationDTO;
        }
    }

    @FXML
    public void onFriendsButtonPressed() {
        mainMenuController.onFriendsButtonClicked();
    }

    @FXML
    public void onFriendRequestsButtonPressed() {
        mainMenuController.openFriendshipRequestMenu();
    }

    @FXML
    public void onMessagesButtonPressed() {
        mainMenuController.openMessagesMenu();
    }

    @FXML
    public void onEventsButtonPressed() {
        mainMenuController.openEventsMenu();
    }

    public void setLoggedUser(UserDTO loggedUser){
        this.loggedUser = loggedUser;
    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.pageService = applicationContext.getPageService();
        this.friendshipService = applicationContext.getFriendshipService();

        initPage();
    }

    void initPage() {
        pageDTO = pageService.getUserPage(loggedUser.getUsername());
        newFriendRequestsCount = pageDTO.getNewFriendRequestCount();
        UserDTO userDTO = pageDTO.getUserDTO();
        userNameLabel.setText(userDTO.getFullName());
        userIDLabel.setText(userDTO.getUsername());
        lastLoginDateLabel.setText(userDTO.getLastLoginAt().format(Constants.DISPLAY_DATE_FORMATTER));

        friendsLabel.setText("Friends (" + pageDTO.getNewFriendsCount() + " new)");
        pageDTO.getFriendDTOList().forEach(friendDTO -> {
            FriendCard friendCard = new FriendCard(friendDTO);
            friendCards.getChildren().add(friendCard.getRoot());
        });

        friendRequestsLabel.setText("Friend requests (" + newFriendRequestsCount + " new)");
        pageDTO.getFriendshipRequestDTOList().forEach(friendRequestDTO -> {
            FriendRequestCard friendRequestCard = new FriendRequestCard(friendRequestDTO);
            friendRequestCards.getChildren().add(friendRequestCard.getRoot());
        });

        messagesLabel.setText("Messages (" + pageDTO.getNewMessagesCount() + " new)");
        pageDTO.getMessageDTOPage().forEach(messageDTO -> {
            MessageCard messageCard = new MessageCard(messageDTO);
            messageCards.getChildren().add(messageCard.getRoot());
        });

        eventsLabel.setText("Events");
        pageDTO.getEventParticipationDTOPage().forEach(eventParticipationDTO -> {
            EventCard eventCard = new EventCard(eventParticipationDTO);
            eventCards.getChildren().add(eventCard.getRoot());
        });

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("../views/notificationView.fxml"));
            Pane layout = fxmlLoader.load();

            notificationArea.setContent(layout);
            NotificationController notificationController = fxmlLoader.getController();
            notificationController.setLoggedUser(loggedUser);
            notificationController.setNotificationService(applicationContext.getNotificationService());
        } catch (IOException e) {
            e.printStackTrace();
        }

        notificationToggle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                notificationArea.setVisible(notificationToggle.isSelected());
                if (notificationToggle.isSelected()) {
                    notificationArea.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
                } else {
                    notificationArea.setPrefSize(0,0);
                }
            }
        });
    }

}
