package uk.co.dajohnston.auth.service;

import java.util.List;
import uk.co.dajohnston.auth.model.User;

public interface UserService {

    void save(User user);

    List<User> getAllUsers();

    void delete(long id);
}
