package utils.events;

import model.dto.MessageDTO;

public class MessageEvent implements Event{
    public enum MessageType{
        RECEIVED, SENT
    }

    MessageType messageType;
    MessageDTO messageDTO;
    ChangeEventType changeType;

    public MessageEvent(MessageDTO messageDTO, ChangeEventType changeType, MessageType messageType){
        this.messageDTO = messageDTO;
        this.changeType = changeType;
        this.messageType = messageType;
    }

    public MessageDTO getMessageDTO(){
        return messageDTO;
    }

    public ChangeEventType getChangeType(){
        return changeType;
    }

    public MessageType getMessageType(){
        return messageType;
    }
}
