package repository.file;

import model.Friendship;
import utils.OrderedTuple;

import java.time.LocalDate;
import java.util.*;

public class FriendshipFile extends AbstractFileRepository<OrderedTuple<String>, Friendship>{
    public FriendshipFile(String filename){
        super(filename);
    }

    @Override
    public Friendship extractEntity(List<String> attributes) {
        String id1 = attributes.get(0);
        String id2 = attributes.get(1);
        LocalDate date = LocalDate.parse(attributes.get(2));
        Friendship friendship = new Friendship(date);
        friendship.setId(new OrderedTuple<>(id1, id2));
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getFirst() + ";" + entity.getId().getSecond() + ";" + entity.getDate();
    }
}
