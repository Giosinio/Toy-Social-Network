package model.dto;

import java.time.LocalDateTime;

public class EventParticipationDTO {
    private final EventDTO eventDTO;
    private final UserDTO userDTO;
    private final LocalDateTime enrollmentDate;
    private final Boolean isSubscribed;

    public EventParticipationDTO(EventDTO eventDTO, UserDTO userDTO, LocalDateTime enrollmentDate, Boolean isSubscribed) {
        this.eventDTO = eventDTO;
        this.userDTO = userDTO;
        this.enrollmentDate = enrollmentDate;
        this.isSubscribed = isSubscribed;
    }

    public EventDTO getEventDTO() {
        return eventDTO;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public Boolean getSubscribed() {
        return isSubscribed;
    }
}
