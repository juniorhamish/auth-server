package uk.co.dajohnston.auth.validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Errors;
import uk.co.dajohnston.auth.model.User;
import uk.co.dajohnston.auth.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    private UserValidator userValidator;
    private User user;
    private Errors errors;

    @Before
    public void setUp() throws Exception {
        userValidator = new UserValidator(userRepository);
        user = new User();
        errors = new DirectFieldBindingResult(user, "user");
    }

    @Test
    public void shouldSupportUserClass() {
        assertTrue(userValidator.supports(User.class));
    }

    @Test
    public void shouldNotSupportClassesOtherThanUser() {
        assertFalse(userValidator.supports(String.class));
    }

    @Test
    public void userShouldHaveEmailAddress() {
        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("emailAddress").getCode();
        assertThat(missingEmailAddressCode, is("Empty.user.emailAddress"));
    }

    @Test
    public void userEmailAddressShouldBeUnique() {
        when(userRepository.findByEmailAddress("dave@test.com")).thenReturn(new User());
        user.setEmailAddress("dave@test.com");

        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("emailAddress").getCode();
        assertThat(missingEmailAddressCode, is("Duplicate.user.emailAddress"));
    }

    @Test
    public void userShouldHaveValidEmailAddress() {
        user.setEmailAddress("foo");

        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("emailAddress").getCode();
        assertThat(missingEmailAddressCode, is("Invalid.user.emailAddress"));
    }

    @Test
    public void userShouldHaveAPassword() {
        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("password").getCode();
        assertThat(missingEmailAddressCode, is("Empty.user.password"));
    }

    @Test
    public void userPasswordMustContainAnUppercaseCharacter() {
        user.setPassword("password1");

        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("password").getCode();
        assertThat(missingEmailAddressCode, is("Invalid.user.password"));
    }

    @Test
    public void userPasswordMustContainALowercaseCharacter() {
        user.setPassword("PASSWORD1");

        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("password").getCode();
        assertThat(missingEmailAddressCode, is("Invalid.user.password"));
    }

    @Test
    public void userPasswordMustContainADigitCharacter() {
        user.setPassword("Password");

        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("password").getCode();
        assertThat(missingEmailAddressCode, is("Invalid.user.password"));
    }

    @Test
    public void userPasswordMustBeAtLeastEightCharactersLong() {
        user.setPassword("Pass1");

        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("password").getCode();
        assertThat(missingEmailAddressCode, is("Invalid.user.password"));
    }

    @Test
    public void userPasswordConfirmFieldMustBeSet() {
        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("passwordConfirm").getCode();
        assertThat(missingEmailAddressCode, is("Diff.user.passwordConfirm"));
    }

    @Test
    public void userPasswordAndPasswordConfirmFieldMustMatch() {
        user.setPassword("Password1");
        user.setPasswordConfirm("Password2");

        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("passwordConfirm").getCode();
        assertThat(missingEmailAddressCode, is("Diff.user.passwordConfirm"));
    }

    @Test
    public void userFirstNameMustBeSet() {
        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("firstName").getCode();
        assertThat(missingEmailAddressCode, is("Empty.user.firstName"));
    }

    @Test
    public void userLastNameMustBeSet() {
        userValidator.validate(user, errors);

        String missingEmailAddressCode = errors.getFieldError("lastName").getCode();
        assertThat(missingEmailAddressCode, is("Empty.user.lastName"));
    }

    @Test
    public void validUserMustHaveNoErrors() {
        user.setFirstName("David");
        user.setLastName("Johnston");
        user.setEmailAddress("david@test.com");
        user.setPassword("Password1234");
        user.setPasswordConfirm("Password1234");

        userValidator.validate(user, errors);

        assertThat(errors.getAllErrors(), is(empty()));
    }

}