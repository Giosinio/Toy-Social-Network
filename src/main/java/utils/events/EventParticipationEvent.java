package utils.events;

import model.EventParticipation;

public class EventParticipationEvent implements ParticipationEventInterface{
    EventParticipation eventParticipation;
    ChangeEventType changeEventType;
    ParticipationEventType participationEventType;

    public EventParticipationEvent(EventParticipation eventParticipation, ChangeEventType changeEventType, ParticipationEventType participationEventType) {
        this.eventParticipation = eventParticipation;
        this.changeEventType = changeEventType;
        this.participationEventType = participationEventType;
    }

    public EventParticipation getEventParticipation() {
        return eventParticipation;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    @Override
    public ParticipationEventType getParticipationEventType() {
        return participationEventType;
    }
}
