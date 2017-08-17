package uk.co.dajohnston.auth.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.co.dajohnston.auth.exception.SignUpException;
import uk.co.dajohnston.auth.filter.JsonWebTokenAuthenticationUtil;
import uk.co.dajohnston.auth.model.Role;
import uk.co.dajohnston.auth.model.User;
import uk.co.dajohnston.auth.service.UserService;
import uk.co.dajohnston.auth.validator.UserValidator;

@RestController
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;
    private final JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator, AuthenticationManager authenticationManager,
            JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.authenticationManager = authenticationManager;
        this.jsonWebTokenAuthenticationUtil = jsonWebTokenAuthenticationUtil;
    }

    @RequestMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User registration(@RequestBody User user, BindingResult bindingResult, HttpServletResponse response) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new SignUpException(bindingResult.getFieldErrors());
        }
        user.setRole(Role.USER);
        userService.save(user);
        authenticateUserAndSetSession(user, response);

        return user;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable Long id) {
        userService.delete(id);
    }

    private void authenticateUserAndSetSession(User user, HttpServletResponse response) {
        String username = user.getEmailAddress();
        String password = user.getPasswordConfirm();
        Authentication authResult = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        String token = jsonWebTokenAuthenticationUtil.getToken(authResult);
        response.addHeader("Authorization", "Bearer " + token);
    }
}
