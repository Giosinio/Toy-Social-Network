package business;

import model.Event;
import model.dto.EventDTO;
import repository.EventParticipationRepository;
import repository.EventRepository;
import repository.PagingEventParticipationRepository;
import repository.PagingEventRepository;
import repository.paging.Page;
import repository.paging.PageableImplementation;
import utils.events.EventParticipationEvent;
import utils.observer.Observer;
import validators.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventService{
    private final PagingEventRepository eventRepository;
    private final PagingEventParticipationRepository eventParticipationRepository;
    private final Validator<Long, Event> eventValidator;
    private final List<Observer<EventParticipationEvent>> observerList;

    public EventService(PagingEventRepository eventRepository, PagingEventParticipationRepository eventParticipationRepository, Validator<Long, Event> eventValidator){
        this.eventRepository = eventRepository;
        this.eventParticipationRepository = eventParticipationRepository;
        this.eventValidator = eventValidator;
        observerList = new ArrayList<>();
    }

    public List<Event> findAllEvents(){
        return eventRepository.findAll();
    }

    public Event findEvent(Long id){
        eventValidator.validateID(id);
        return eventRepository.findOne(id);
    }

    public Event addEvent(Event event){
        eventValidator.validateEntity(event);
        return eventRepository.save(event);
    }

    public Event removeEvent(Long id){
        eventValidator.validateID(id);
        return eventRepository.remove(id);
    }

    /**
     * method that uses EventRepository to get all Events that a certain User doesn't attend to
     * @param username - String(User ID)
     * @return - List(Event)
     */
    public List<EventDTO> getRecommendedEventsForUser(String username){
        LocalDate currentTime = LocalDate.now();
        List<Event> eventList =  eventRepository.getRecommendedEventsForUser(username, currentTime);
        return eventList.stream().map(this::createEventDTO).toList();
    }

    /**
     * method that receives an Event entity and converts it into an EventDTO one
     * @param event - Event
     * @return - EventDTO
     */
    public EventDTO createEventDTO(Event event){
        int numberParticipants = eventParticipationRepository.getNumberOfParticipantsForEvent(event.getId());
        EventDTO eventDTO = new EventDTO(event.getEventName(), event.getStartDate(), event.getEndDate(), event.getOrganiser(), event.getCategory(), event.getDescription(), event.getLocation(), numberParticipants);
        eventDTO.setId(event.getId());
        return eventDTO;
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
     * method that returns the list of EventDTO for a given page number
     * @param username - String(User ID)
     * @param pageNumber - int
     * @return - List of EventDTO
     */
    public List<EventDTO> getPageRecommendedEventsForUser(String username, int pageNumber){
        LocalDate currentTime = LocalDate.now();
        Page<Event> eventPage = eventRepository.getRecommendedEventsForUser(username, currentTime,  new PageableImplementation(pageNumber, this.pageSize));
        return eventPage.getContent().map(this::createEventDTO).toList();
    }

    /**
     * method that returns the number of pages the recommended events will be displayed for a given User
     * @param username - String(User ID)
     * @return - int
     */
    public int getNumberOfPagesEvents(String username){
        int numberOfEvents = eventRepository.getNumberOfRecommendedEventsForUser(username, LocalDate.now());
        if(numberOfEvents == 0)
            return 0;
        if(numberOfEvents % pageSize == 0)
            return numberOfEvents / pageSize;
        return 1 + (numberOfEvents / pageSize);
    }
}
