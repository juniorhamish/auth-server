package uk.co.dajohnston.auth;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.co.dajohnston.auth.model.Role;
import uk.co.dajohnston.auth.repository.RoleRepository;

@SpringBootApplication
@SuppressWarnings("PMD.UseUtilityClass")
public class Application {

    @Autowired
    private RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    private void createRoles() {
        if (!roleExists("ADMIN")) {
            createRole("ADMIN");
        }
        if (!roleExists("USER")) {
            createRole("USER");
        }
    }

    private void createRole(String name) {
        Role role = new Role();
        role.setName(name);
        roleRepository.save(role);
    }

    private boolean roleExists(String admin) {
        return roleRepository.findByName(admin) != null;
    }

}
