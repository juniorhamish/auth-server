package uk.co.dajohnston.auth.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;
import uk.co.dajohnston.auth.exception.SignupException;
import uk.co.dajohnston.auth.model.User;
import uk.co.dajohnston.auth.service.UserService;
import uk.co.dajohnston.auth.validator.UserValidator;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Mock
    private UserService userService;
    @Mock
    private UserValidator userValidator;
    @Mock
    private BindingResult bindingResult;
    private UserController userController;
    private final User user = new User();

    @Before
    public void setUp() throws Exception {
        userController = new UserController(userService, userValidator);
    }

    @Test
    public void shouldGetAllUsersFromRepository() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userService.getAllUsers()).thenReturn(users);

        assertThat(userController.getUsers(), is(users));
    }

    @Test
    public void shouldSaveUserIfValidationSucceeds() {
        when(bindingResult.hasErrors()).thenReturn(false);

        userController.registration(user, bindingResult);

        verify(userService).save(user);
    }

    @Test
    public void shouldThrowExceptionIfValidationFails() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(userValidator.getErrorMessage(bindingResult)).thenReturn("This is the error");

        exception.expect(SignupException.class);
        exception.expectMessage(is("This is the error"));

        userController.registration(user, bindingResult);
    }

    @Test
    public void shouldValidateThenCheckForErrors() {
        userController.registration(user, bindingResult);

        InOrder inOrder = Mockito.inOrder(userValidator, bindingResult);
        inOrder.verify(userValidator).validate(user, bindingResult);
        inOrder.verify(bindingResult).hasErrors();

    }

}
