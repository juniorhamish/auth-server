package uk.co.dajohnston.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.dajohnston.auth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
