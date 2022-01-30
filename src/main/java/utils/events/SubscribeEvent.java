package utils.events;

import model.dto.EventParticipationDTO;

public class SubscribeEvent implements ParticipationEventInterface{
    private EventParticipationDTO eventParticipationDTO;
    private ChangeEventType changeEventType;
    private ParticipationEventType participationEventType;

    public SubscribeEvent(EventParticipationDTO eventParticipationDTO, ChangeEventType changeEventType, ParticipationEventType participationEventType) {
        this.eventParticipationDTO = eventParticipationDTO;
        this.changeEventType = changeEventType;
        this.participationEventType = participationEventType;
    }

    public EventParticipationDTO getEventParticipationDTO() {
        return eventParticipationDTO;
    }

    public ChangeEventType getChangeEventType() {
        return changeEventType;
    }

    @Override
    public ParticipationEventType getParticipationEventType() {
        return participationEventType;
    }
}
