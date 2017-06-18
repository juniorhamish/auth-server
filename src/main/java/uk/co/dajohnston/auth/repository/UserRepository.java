package uk.co.dajohnston.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.dajohnston.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAddress(String emailAddress);
}
