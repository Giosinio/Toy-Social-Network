package controller;

import business.EventParticipationService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.dto.EventParticipationDTO;
import model.dto.UserDTO;
import utils.config.ApplicationContext;
import utils.events.ParticipationEventInterface;
import utils.observer.Observer;

import java.util.Arrays;
import java.util.List;

public class MyEventsController implements Observer<ParticipationEventInterface> {
    private UserDTO loggedUser;
    private final ObservableList<MyEventCell> model = FXCollections.observableArrayList();
    private EventParticipationService eventParticipationService;
    private int currentPage, numberOfPages;


    private class MyEventCell extends AnchorPane{
        private final EventParticipationDTO eventParticipationDTO;
        VBox mainPane;
        HBox buttonsPane;
        Button subscribeButton, renounceButton;
        Label noParticipants = new Label(), name = new Label(), period = new Label(), location = new Label(), organization = new Label(), category = new Label();
        TextArea description = new TextArea();

        private MyEventCell(EventParticipationDTO eventParticipationDTO){
            super();
            this.eventParticipationDTO = eventParticipationDTO;
            this.setHeight(450.0);
            this.setWidth(250.0);
            name.setPrefWidth(200.0);
            name.setMinHeight(25.0);
            name.setText(eventParticipationDTO.getEventDTO().getEventName());
            AnchorPane.setLeftAnchor(name, 100.0);
            setMainPaneComponents();
            setButtonsProperties();
            this.getChildren().addAll(name, mainPane, buttonsPane);
        }

        private void setMainPaneComponents(){
            mainPane = new VBox();
            AnchorPane.setTopAnchor(mainPane,75.0);
            Arrays.asList(noParticipants, period, location, organization, category, description).forEach(x -> x.setPrefWidth(200.0));
            description.setWrapText(true);
            description.setMaxHeight(100.0); description.setMinHeight(100.0);
            mainPane.setSpacing(10.0);
            mainPane.getChildren().addAll(noParticipants, period, location, organization, category, description);
            noParticipants.setText("Participants: " + eventParticipationDTO.getEventDTO().getEventParticipants());
            period.setText(eventParticipationDTO.getEventDTO().getStartDate().toString() + " - " + eventParticipationDTO.getEventDTO().getEndDate());
            location.setText(eventParticipationDTO.getEventDTO().getLocation());
            organization.setText(eventParticipationDTO.getEventDTO().getOrganiser());
            category.setText(eventParticipationDTO.getEventDTO().getCategory());
            description.setText(eventParticipationDTO.getEventDTO().getDescription());
        }

        private void setButtonsProperties(){
            buttonsPane = new HBox();
            buttonsPane.setSpacing(10.0);
            AnchorPane.setBottomAnchor(buttonsPane, 15.0);
            AnchorPane.setRightAnchor(buttonsPane, 15.0);
            subscribeButton = new Button();
            if(this.eventParticipationDTO.getSubscribed())
                subscribeButton.setText("Unsubscribe");
            else
                subscribeButton.setText("Subscribe");
            subscribeButton.setOnAction(event -> eventParticipationService.changeSubscriptionStateEventForUser(loggedUser.getUsername(), this.eventParticipationDTO.getEventDTO(), !eventParticipationDTO.getSubscribed()));

            renounceButton = new Button("Renounce");
            renounceButton.setOnAction(ev -> eventParticipationService.removeEventParticipation(eventParticipationDTO.getEventDTO().getId(), eventParticipationDTO.getUserDTO().getUsername()));
            buttonsPane.getChildren().addAll(renounceButton, subscribeButton);
        }
    }


    @FXML
    private Button previousPageButton, nextPageButton;

    @FXML
    private void onPreviousPageButtonClicked(){
        if(currentPage > 1){
            currentPage--;
            currentPageTextField.setText(String.valueOf(currentPage));
        }
        setPreviousPageButtonState();
        setNextPageButtonState();

        updateModel();
    }

    @FXML
    private void onNextPageButtonClicked(){
        if (currentPage < numberOfPages){
            currentPage++;
            currentPageTextField.setText(String.valueOf(currentPage));

            setPreviousPageButtonState();
            setNextPageButtonState();

            updateModel();
        }
    }

    private void setPreviousPageButtonState(){
        previousPageButton.setDisable(currentPage == 1 || numberOfPages == 0);
    }

    private void setNextPageButtonState(){
        nextPageButton.setDisable(currentPage == numberOfPages || numberOfPages == 0);
    }

    @FXML
    private TextField currentPageTextField, numberOfPagesTextField;

    public void setLoggedUser(UserDTO loggedUser){
        this.loggedUser = loggedUser;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.eventParticipationService = applicationContext.getEventParticipationService();
        eventParticipationService.addObserver(this);
        initMyEventsPage();
        updateModel();
    }

    private void initMyEventsPage(){
        numberOfPages = eventParticipationService.getNumberOfPagesEventsUserParticipatesIn(loggedUser.getUsername());
        if(numberOfPages == 0){
            currentPage = 0;
            currentPageTextField.setText("0");
            numberOfPagesTextField.setText("0");
        }
        else{
            currentPage = 1;
            currentPageTextField.setText(String.valueOf(currentPage));
            numberOfPagesTextField.setText(String.valueOf(numberOfPages));
        }
        setPreviousPageButtonState();
        setNextPageButtonState();
    }

    /**
     * method that request the updated information for the current page of EventParticipationDTO
     * Since the pages in UI are indexed from 1 to numberOfPages and in memory from 0 to numberOfPages - 1,
     * the controller has to request information from the page with index currentPage - 1
     */
    private void updateModel(){
        if(currentPage != 0){
            List<EventParticipationDTO> eventParticipationDTOList = eventParticipationService.getPageEventsUserParticipatesIn(loggedUser.getUsername(), currentPage-1);
            List<MyEventCell> myEventCellList = eventParticipationDTOList.stream().map(MyEventCell::new).toList();
            model.setAll(myEventCellList);
        }
        else
            model.clear();
    }

    @FXML
    private ListView<MyEventCell> eventsListView;

    @FXML
    private void initialize(){
        eventsListView.setItems(model);
    }

    private void updateEventsPage(){
        numberOfPages = eventParticipationService.getNumberOfPagesEventsUserParticipatesIn(loggedUser.getUsername());
        if(numberOfPages == 0){
            currentPage = 0;
            currentPageTextField.setText("0");
            numberOfPagesTextField.setText("0");
            previousPageButton.setDisable(true);
            nextPageButton.setDisable(true);
        }
        else{
            currentPage = Math.min(currentPage, numberOfPages);
            currentPageTextField.setText(String.valueOf(currentPage));
            numberOfPagesTextField.setText(String.valueOf(numberOfPages));
            setPreviousPageButtonState();
            setNextPageButtonState();
        }
    }

    @Override
    public void update(ParticipationEventInterface event) {
        updateEventsPage();
        updateModel();

    }
}
