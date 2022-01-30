package model.dto;

import model.Event;

import java.time.LocalDate;

public class EventDTO extends Event {
    private final Integer eventParticipants;
    public EventDTO(String eventName, LocalDate startDate, LocalDate endDate, String organiser, String category, String description, String location, Integer eventParticipants){
        super(eventName, startDate, endDate, organiser, category, description, location);
        this.eventParticipants = eventParticipants;
    }

    public EventDTO(Event event, Integer eventParticipants){
        super(event.getEventName(), event.getStartDate(), event.getEndDate(), event.getOrganiser(), event.getCategory(), event.getDescription(), event.getLocation());
        this.setId(event.getId());
        this.eventParticipants = eventParticipants;
    }

    public Integer getEventParticipants() {
        return eventParticipants;
    }
}
