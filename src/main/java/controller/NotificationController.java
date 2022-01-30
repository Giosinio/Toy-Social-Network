package controller;

import business.NotificationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.dto.NotificationDTO;
import model.dto.UserDTO;
import utils.events.ChangeEventType;
import utils.events.NotificationEvent;
import utils.observer.Observer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationController implements Observer<NotificationEvent> {
    private ObservableList<NotificationCell> modelNotifications = FXCollections.observableArrayList();
    private NotificationService notificationService;
    private UserDTO loggedUser;

    private class NotificationCell extends AnchorPane {
        private final NotificationDTO notificationDTO;
        private final GridPane notificationPane = new GridPane();
        private final Label notificationLabel = new Label();
        private final Label daysAgoLabel = new Label();
        private final Button deleteButton = new Button("X");

        public NotificationCell(NotificationDTO notificationDTO){
            super();
            this.notificationDTO = notificationDTO;
            this.getChildren().addAll(notificationPane, deleteButton);
            AnchorPane.setLeftAnchor(notificationPane, 10.0);
            AnchorPane.setRightAnchor(deleteButton, 10.0);
            setNotificationPane();
            setDeleteButtonProperty();
        }

        public NotificationDTO getNotificationDTO(){
            return notificationDTO;
        }

        private void setDaysAgoLabel() {
            long dateDiff = ChronoUnit.DAYS.between(notificationDTO.getDisplayDate(), LocalDate.now());
            String text;
            if(dateDiff == 0)
                text = "Today";
            else if(dateDiff == 1)
                text = "Yesterday";
            else
                text = dateDiff + " days ago";
            daysAgoLabel.setText(text);
        }

        private void setNotificationPane(){
            notificationPane.add(notificationLabel, 0, 0);
            notificationLabel.setText(notificationDTO.toString());
            notificationPane.add(daysAgoLabel, 0, 1);
            setDaysAgoLabel();
        }

        private void setDeleteButtonProperty(){
            deleteButton.setOnAction(event -> {
                notificationService.removeNotification(notificationDTO.getNotificationID());
            });
        }
    }

    @FXML
    private ListView<NotificationCell> notificationListView;

    public void setNotificationService(NotificationService notificationService){
        this.notificationService = notificationService;
        this.notificationService.addObserver(this);
        initModel();
    }

    public void setLoggedUser(UserDTO loggedUser){
        this.loggedUser = loggedUser;
    }

    @FXML
    private void initialize(){
        notificationListView.setItems(modelNotifications);
    }

    private void initModel(){
        List<NotificationDTO> notificationDTOList = notificationService.getNotificationListForUser(loggedUser);
        List<NotificationCell> notificationCellList = notificationDTOList.stream().map(NotificationCell::new).toList();
        modelNotifications.setAll(notificationCellList);
    }

    @Override
    public void update(NotificationEvent notificationEvent) {
        if(notificationEvent != null){
            if(notificationEvent.getChangeEventType().equals(ChangeEventType.REMOVE)){
                List<NotificationCell> filteredNotifications = modelNotifications.stream().filter(n -> !(n.getNotificationDTO().getNotificationID().equals(notificationEvent.getNotification().getId()))).collect(Collectors.toList());
                modelNotifications.setAll(filteredNotifications);
            }
        }
    }
}
