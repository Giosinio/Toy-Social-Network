package controller;

import business.EventParticipationService;
import business.EventService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.dto.EventDTO;
import model.dto.EventParticipationDTO;
import model.dto.UserDTO;
import utils.config.ApplicationContext;
import utils.events.ParticipationEventInterface;
import utils.events.ParticipationEventType;
import utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class DiscoverEventsController implements Observer<ParticipationEventInterface> {
    private UserDTO loggedUser;
    private EventService eventService;
    private EventParticipationService eventParticipationService;
    private int currentPage, numberOfPages;
    private ApplicationContext applicationContext;

    private class DiscoverEventCell extends AnchorPane {
        private EventDTO eventDTO;
        VBox mainPane;
        Button participateButton;
        Label noParticipants = new Label(), name = new Label(), period = new Label(), location = new Label(), organization = new Label(), category = new Label();
        TextArea description = new TextArea();
        private DiscoverEventCell(EventDTO eventDTO){
            super();
            this.getStyleClass().add("discover-event-cell");
            this.eventDTO = eventDTO;
            this.setHeight(850.0);
            this.setWidth(250.0);
            name.setPrefWidth(200.0);
            name.setMinHeight(100.0);
            AnchorPane.setLeftAnchor(name, 50.0);
            name.setText(eventDTO.getEventName());
            setMainPaneComponents();
            setButtonProperties();
            this.getChildren().addAll(name, mainPane, participateButton);
        }

        private void setMainPaneComponents(){
            mainPane = new VBox();
            mainPane.getStyleClass().add("discover-event-cell-main-pane");
            AnchorPane.setTopAnchor(mainPane,75.0);
            Arrays.asList(noParticipants, period, location, organization, category, description).forEach(x -> x.setPrefWidth(200.0));
            description.setWrapText(true);
            description.setMaxHeight(100.0); description.setMinHeight(100.0);
            mainPane.setSpacing(10.0);
            mainPane.getChildren().addAll(noParticipants, period, location, organization, category, description);
            noParticipants.setText("Participants: " + eventParticipationService.getNumberOfParticipantsForEvents(eventDTO.getId()));
            period.setText(eventDTO.getStartDate().toString() + " - " + eventDTO.getEndDate());
            location.setText(eventDTO.getLocation());
            organization.setText(eventDTO.getOrganiser());
            category.setText(eventDTO.getCategory());
            description.setText(eventDTO.getDescription());
        }

        private void setButtonProperties(){
            participateButton = new Button("Participate");
            AnchorPane.setBottomAnchor(participateButton, 10.0);
            AnchorPane.setRightAnchor(participateButton, 10.0);
            participateButton.setOnAction(event -> eventParticipationService.addEventParticipation(new EventParticipationDTO(this.eventDTO, loggedUser, LocalDateTime.now(), true)));
        }
    }

    @FXML
    private HBox eventsHBox;

    @FXML
    private Button previousPageButton, nextPageButton;

    private void setPreviousPageButtonState(){
        previousPageButton.setDisable(currentPage == 1);
    }

    private void setNextPageButtonState(){
        nextPageButton.setDisable(currentPage == numberOfPages);
    }

    @FXML
    private void onPreviousPageButtonClicked(){
        if(currentPage > 1){
            currentPage--;
            currentPageTextField.setText(String.valueOf(currentPage));

            setPreviousPageButtonState();
            setNextPageButtonState();

            updateModel();
        }
    }

    @FXML
    private void onNextPageButtonClicked(){
        if(currentPage < numberOfPages){
            currentPage++;
            currentPageTextField.setText(String.valueOf(currentPage));

            setPreviousPageButtonState();
            setNextPageButtonState();

            updateModel();
        }
    }

    @FXML
    private TextField currentPageTextField, numberOfPagesTextField;

    @FXML
    private void initialize(){

    }

    /**
     * method that request the updated information for the current page of EventDTO
     * Since the pages in UI are indexed from 1 to numberOfPages and in memory from 0 to numberOfPages - 1,
     * the controller has to request information from the page with index currentPage - 1
     */
    private void updateModel(){
        eventsHBox.getChildren().clear();
        if(currentPage != 0){
            List<EventDTO> events = eventService.getPageRecommendedEventsForUser(loggedUser.getUsername(), currentPage - 1);
            List<DiscoverEventCell> eventCells = events.stream().map(DiscoverEventCell::new).toList();
            eventsHBox.getChildren().addAll(eventCells);
        }
    }

    public void setLoggedUser(UserDTO loggedUser){
        this.loggedUser = loggedUser;
    }

    private void initEventsPage(){
        numberOfPages = eventService.getNumberOfPagesEvents(loggedUser.getUsername());
        if(numberOfPages == 0){
            currentPage = 0;
            numberOfPagesTextField.setText("0");
            currentPageTextField.setText("0");
        }
        else{
            currentPage = 1;
            numberOfPagesTextField.setText(String.valueOf(numberOfPages));
            currentPageTextField.setText(String.valueOf(currentPage));
            if(currentPage != numberOfPages)
                nextPageButton.setDisable(false);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.eventService = applicationContext.getEventService();
        this.eventParticipationService = applicationContext.getEventParticipationService();
        eventParticipationService.addObserver(this);

        initEventsPage();
        updateModel();
    }

    private void updateEventsPage(){
        numberOfPages = eventService.getNumberOfPagesEvents(loggedUser.getUsername());
        if(numberOfPages == 0){
            currentPage = 0;
            numberOfPagesTextField.setText("0");
            currentPageTextField.setText("0");
        }
        else{
            currentPage = Math.min(currentPage, numberOfPages);
            numberOfPagesTextField.setText(String.valueOf(numberOfPages));
            currentPageTextField.setText(String.valueOf(currentPage));
            setPreviousPageButtonState();
            setNextPageButtonState();
        }
    }

    @Override
    public void update(ParticipationEventInterface event) {
        if(event.getParticipationEventType().equals(ParticipationEventType.PARTICIPATE)){
            updateEventsPage();
            updateModel();
        }
    }

}
