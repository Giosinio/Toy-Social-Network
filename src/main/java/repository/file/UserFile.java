package repository.file;

import model.User;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class UserFile extends AbstractFileRepository<String, User> {
    public UserFile(String filename) {
        super(filename);
    }

    @Override
    public User extractEntity(List<String> attributes){
        User user = new User(attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4), null);
        user.setId(attributes.get(0));
        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}
