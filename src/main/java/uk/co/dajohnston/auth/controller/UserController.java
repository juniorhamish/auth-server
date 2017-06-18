package uk.co.dajohnston.auth.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

    private UserService userService;
    private UserValidator userValidator;
    private ResourceBundleMessageSource messageSource;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator, ResourceBundleMessageSource messageSource) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.messageSource = messageSource;
    }

    @RequestMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public User registration(@RequestBody User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            String errorMessage = generateErrorMessage(bindingResult);
            throw new SignupException(errorMessage);
        }
        userService.save(user);

        return user;
    }

    private String generateErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errorMessage.append(error.getField()).append(" : ").append(messageSource.getMessage(error, null))
                    .append(System.lineSeparator());
        }
        return errorMessage.toString();
    }
}
