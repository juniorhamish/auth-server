package uk.co.dajohnston.auth.validator;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import uk.co.dajohnston.auth.model.User;
import uk.co.dajohnston.auth.repository.UserRepository;

@Component
public class UserValidator implements Validator {

    private static final String FIELD_EMAIL_ADDRESS = "emailAddress";
    private static final String FIELD_PASSWORD = "password";
    private static final String VALID_PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    private UserRepository userRepository;
    private ResourceBundleMessageSource messageSource;

    @Autowired
    public UserValidator(UserRepository userRepository, ResourceBundleMessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        ValidationUtils
                .rejectIfEmptyOrWhitespace(errors, FIELD_EMAIL_ADDRESS, "Empty.user.emailAddress", "Email Address must be provided.");
        if (userRepository.findByEmailAddress(user.getEmailAddress()) != null) {
            errors.rejectValue(FIELD_EMAIL_ADDRESS, "Duplicate.user.emailAddress", "The email address is already in use.");
        }
        if (!EmailValidator.getInstance().isValid(user.getEmailAddress())) {
            errors.rejectValue(FIELD_EMAIL_ADDRESS, "Invalid.user.emailAddress", "The email address is invalid.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, FIELD_PASSWORD, "Empty.user.password", "Password must be provided.");
        if (!user.getPassword().matches(VALID_PASSWORD_REGEX)) {
            errors.rejectValue(FIELD_PASSWORD, "Invalid.user.password", "The password is not valid.");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.user.passwordConfirm", "The passwords do not match.");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "Empty.user.firstName", "First name must be provided.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Empty.user.lastName", "Last name must be provided.");
    }

    public String getErrorMessage(Errors errors) {
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError error : errors.getFieldErrors()) {
            errorMessage.append(error.getField()).append(" : ").append(messageSource.getMessage(error, null))
                    .append(System.lineSeparator());
        }
        return errorMessage.toString();
    }
}
