package model.dto;

import java.time.LocalDate;

public class FriendDTO {
    private final UserDTO friend;
    private final LocalDate date;

    public FriendDTO(UserDTO friend, LocalDate date){
        this.friend = friend;
        this.date = date;
    }

    public UserDTO getFriend() {
        return friend;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString(){
        return friend + " since " + date;
    }
}
