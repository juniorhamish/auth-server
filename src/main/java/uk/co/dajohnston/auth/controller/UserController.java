package uk.co.dajohnston.auth.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.co.dajohnston.auth.exception.SignupException;
import uk.co.dajohnston.auth.model.User;
import uk.co.dajohnston.auth.service.UserService;
import uk.co.dajohnston.auth.validator.UserValidator;

@RestController
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @RequestMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public User registration(@RequestBody User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new SignupException(userValidator.getErrorMessage(bindingResult));
        }
        userService.save(user);

        return user;
    }
}
