package model;

import java.time.LocalDate;
import java.util.Objects;

public class Event extends Entity<Long>{
    private final String eventName;
    private final LocalDate startDate, endDate;
    private final String organiser, category, description, location;

    public Event(String eventName, LocalDate startDate, LocalDate endDate, String organiser, String category, String description, String location) {
        this.eventName = eventName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organiser = organiser;
        this.category = category;
        this.description = description;
        this.location = location;
    }

    public String getEventName() {
        return eventName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getOrganiser() {
        return organiser;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int hashCode(){
        return Objects.hash(super.id);
    }

    @Override
    public boolean equals(Object object){
        if(object == null)
            return false;
        if(!(object instanceof Event event))
            return false;
        return this.id.equals(event.id);
    }
}
