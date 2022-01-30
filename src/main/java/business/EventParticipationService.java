package business;

import exceptions.EventParticipationException;
import model.Event;
import model.EventParticipation;
import model.Notification;
import model.User;
import model.dto.EventDTO;
import model.dto.EventParticipationDTO;
import model.dto.UserDTO;
import repository.*;
import repository.paging.Page;
import repository.paging.PageableImplementation;
import utils.Tuple;
import utils.events.*;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventParticipationService implements Observable<ParticipationEventInterface> {
    private final Repository<String, User> userRepository;
    private final PagingEventRepository eventRepository;
    private final PagingEventParticipationRepository eventParticipationRepository;
    private final NotificationRepository notificationRepository;
    private final Validator<Tuple<Long, String>, EventParticipation> eventParticipationValidator;
    private final List<Observer<ParticipationEventInterface>> observerList;

    public EventParticipationService(Repository<String, User> userRepository, PagingEventRepository eventRepository, PagingEventParticipationRepository eventParticipantsRepository, NotificationRepository notificationRepository, Validator<Tuple<Long, String>, EventParticipation> eventParticipantValidator){
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventParticipationRepository = eventParticipantsRepository;
        this.notificationRepository = notificationRepository;
        this.eventParticipationValidator = eventParticipantValidator;
        this.observerList = new ArrayList<>();
    }

    /**
     * method that adds a new EventParticipation
     * @param participationDTO - EventParticipationDTO(used to transfer data)
     * @return - null, if the participation was stored successfully
     *           EventParticipation, otherwise
     */
    public EventParticipation addEventParticipation(EventParticipationDTO participationDTO){
        LocalDate currentTime = LocalDate.now();
        EventParticipation eventParticipation = new EventParticipation(participationDTO.getEventDTO().getId(), participationDTO.getUserDTO().getUsername(), participationDTO.getEnrollmentDate(), participationDTO.getSubscribed(), currentTime);
        eventParticipationValidator.validateID(eventParticipation.getId());
        eventParticipationValidator.validateEntity(eventParticipation);
        EventParticipation result = eventParticipationRepository.save(eventParticipation);
        if(result == null){
            notifyAll(new EventParticipationEvent(null, ChangeEventType.ADD, ParticipationEventType.PARTICIPATE));
            Long eventID = participationDTO.getEventDTO().getId();
            Event event = eventRepository.findOne(eventID);
            LocalDate startDate = event.getStartDate();
            String username = participationDTO.getUserDTO().getUsername();
            List<LocalDate> displayDates = Arrays.asList(startDate, startDate.minusDays(1), startDate.minusDays(3));
            displayDates = displayDates.stream().filter(date -> date.compareTo(currentTime) >= 0).toList();
            for(LocalDate displayDate : displayDates){
                if (notificationRepository.findNotification(eventID, username, displayDate) == null)
                    notificationRepository.save(new Notification(eventID, username, displayDate, false));
            }

        }
        return result;
    }

    public EventParticipation removeEventParticipation(Long eventID, String userID){
        EventParticipation result = eventParticipationRepository.remove(new Tuple<>(eventID, userID));
        if(result != null){
            notifyAll(new EventParticipationEvent(result, ChangeEventType.REMOVE, ParticipationEventType.PARTICIPATE));
            //nu ii neaparat un bug: daca de exemplu, userul George da 'Participa' si apoi 'Renunta' la un eveniment de mai multe ori si e de exemplu in ziua 3(in care ar tb sa primeasca notificare), acesta primeste notificare de fiecare data cand da Participa/Renunta
            notificationRepository.deleteNotificationUserRenounce(userID, eventID, LocalDate.now());
        }
        return result;
    }

    /**
     * method that returns all the Events a given User participates to
     * @param username - String(User ID)
     * @return - List of EventParticipationDTO
     */
    public List<EventParticipationDTO> getEventsUserParticipatesIn(String username){
        List<EventParticipation> eventParticipations = eventParticipationRepository.getEventsUserParticipatesIn(username, LocalDate.now());
        return eventParticipations.stream().map(this::createEventParticipationDTO).toList();
    }

    /**
     * method that changes whether a User wants to receive notifications for an Event he participates to
     * @param username - String(User ID)
     * @param event - Event
     * @param newState - Boolean
     */
    public void changeSubscriptionStateEventForUser(String username, Event event, Boolean newState){
        LocalDate currentTime = LocalDate.now();
        EventParticipation result = eventParticipationRepository.update(new EventParticipation(event.getId(), username, null, newState, currentTime));
        EventParticipationDTO resultDTO = createEventParticipationDTO(result);
        if(result != null)
            throw new EventParticipationException("The subscription didn't occur!");
        notifyAll(new SubscribeEvent(resultDTO, ChangeEventType.UPDATE, ParticipationEventType.SUBSCRIBE));
        if(newState){
            notificationRepository.changeDisableStatusWhenSubscriptionChange(event.getId(), username, LocalDate.now(), false);
        }
        else{
            notificationRepository.changeDisableStatusWhenSubscriptionChange(event.getId(), username, LocalDate.now(), true);
        }

    }

    /**
     * method that returns the number of Users that participate for the given event
     * @param eventID - long
     * @return - int
     */
    public int getNumberOfParticipantsForEvents(Long eventID){
        return eventParticipationRepository.getNumberOfParticipantsForEvent(eventID);
    }

    /**
     * method that receives an EventParticipation entity and converts it into a DTO
     * @param eventParticipation - EventParticipation
     * @return - EventParticipationDTO
     */
    private EventParticipationDTO createEventParticipationDTO(EventParticipation eventParticipation){
        if(eventParticipation == null)
            return null;
        Event event = eventRepository.findOne(eventParticipation.getId().getFirst());
        if(event == null)
            return null;
        UserDTO userDTO = UserService.createUserDTO(userRepository.findOne(eventParticipation.getId().getSecond()));
        if (userDTO == null)
            return null;
        EventDTO eventDTO = new EventDTO(event, eventParticipationRepository.getNumberOfParticipantsForEvent(event.getId()));
        return new EventParticipationDTO(eventDTO, userDTO, eventParticipation.getEnrollmentDate(), eventParticipation.getSubscribed());
    }

    @Override
    public void addObserver(Observer<ParticipationEventInterface> observer) {
        observerList.add(observer);
    }

    @Override
    public void removeObserver(Observer<ParticipationEventInterface> observer) {
        observerList.remove(observer);
    }

    @Override
    public void notifyAll(ParticipationEventInterface event) {
        observerList.forEach(x -> x.update(event));
    }

    private int pageSize;

    /**
     * method that sets the page size
     * @param pageSize - int
     */
    public void setPageSize(int pageSize){
        this.pageSize = pageSize;
    }

    /**
     * method that returns a list of the EventParticipationDTO that are on a given page number for a given User
     * @param username - String(User ID)
     * @param pageNumber - int
     * @return - List of EventParticipationDTO
     */
    public List<EventParticipationDTO> getPageEventsUserParticipatesIn(String username, int pageNumber){
        Page<EventParticipation> eventParticipationPage = eventParticipationRepository.getEventsUserParticipatesIn(username, LocalDate.now(), new PageableImplementation(pageNumber, pageSize));
        List<EventParticipation> eventParticipationList = eventParticipationPage.getContent().toList();
        return eventParticipationList.stream().map(this::createEventParticipationDTO).toList();
    }

    /**
     * method that returns the number of pages the events that a certain user participates to will be displayed
     * @param username - String(User ID)
     * @return - int
     */
    public int getNumberOfPagesEventsUserParticipatesIn(String username){
        int numberOfEvents = eventParticipationRepository.getNumberOfEventsUserParticipatesIn(username, LocalDate.now());
        if(numberOfEvents == 0)
            return 0;
        if(numberOfEvents % pageSize == 0)
            return numberOfEvents / pageSize;
        return 1 + numberOfEvents / pageSize;
    }
}
