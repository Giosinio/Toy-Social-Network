package controller;

import business.EventService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Event;
import model.dto.UserDTO;
import utils.config.ApplicationContext;

import java.time.LocalDate;

public class AddEventController{
    private EventService eventService;
    private ApplicationContext applicationContext;
    private UserDTO loggedUser;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.eventService = applicationContext.getEventService();
    }

    public void setLoggedUser(UserDTO loggedUser){
        this.loggedUser = loggedUser;
    }

    @FXML
    private TextField eventNameTextField, locationTextField, organizationTextField, categoryTextField;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private DatePicker startDatePicker, endDatePicker;

    @FXML
    private void onDiscardButtonClicked(){
        Stage stage = (Stage) startDatePicker.getScene().getWindow();
        stage.close();
    }

    private void clearNewEventArea(){
        eventNameTextField.setText("");
        locationTextField.setText("");
        organizationTextField.setText("");
        categoryTextField.setText("");
        descriptionTextArea.setText("");
    }

    @FXML
    private void onAddEventButtonClicked(){
        String eventName = eventNameTextField.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String organizer = organizationTextField.getText();
        String category = categoryTextField.getText();
        String description = descriptionTextArea.getText();
        String location = locationTextField.getText();
        try{
            Event event = new Event(eventName, startDate, endDate, organizer, category, description, location);
            eventService.addEvent(event);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event created successfully!");
            alert.show();
            clearNewEventArea();
        }
        catch (RuntimeException exception){
            Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage());
            alert.showAndWait();
        }
    }
}
