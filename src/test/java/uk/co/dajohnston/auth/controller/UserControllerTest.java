package uk.co.dajohnston.auth.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.validation.BindingResult;
import uk.co.dajohnston.auth.exception.SignUpException;
import uk.co.dajohnston.auth.filter.JsonWebTokenAuthenticationUtil;
import uk.co.dajohnston.auth.model.Role;
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
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil;
    @Mock
    private HttpServletResponse httpServletResponse;
    private UserController userController;
    private final User user = new User();

    @Before
    public void setUp() throws Exception {
        userController = new UserController(userService, userValidator, authenticationManager, jsonWebTokenAuthenticationUtil);
    }

    @Test
    public void shouldGetAllUsersFromRepository() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userService.getAllUsers()).thenReturn(users);

        assertThat(userController.getUsers(), is(users));
    }

    @Test
    public void shouldSaveUserWithUserRoleIfValidationSucceeds() {
        when(bindingResult.hasErrors()).thenReturn(false);

        user.setRole(Role.ADMIN);
        userController.registration(user, bindingResult, httpServletResponse);

        verify(userService).save(user);
        assertThat(user.getRole(), is(Role.USER));
    }

    @Test
    public void shouldThrowExceptionIfValidationFails() {
        when(bindingResult.hasErrors()).thenReturn(true);

        exception.expect(SignUpException.class);

        userController.registration(user, bindingResult, httpServletResponse);
    }

    @Test
    public void shouldValidateThenCheckForErrors() {
        userController.registration(user, bindingResult, httpServletResponse);

        InOrder inOrder = Mockito.inOrder(userValidator, bindingResult);
        inOrder.verify(userValidator).validate(user, bindingResult);
        inOrder.verify(bindingResult).hasErrors();
    }

    @Test
    public void shouldRemoveUserUsingUserServiceOnDelete() {
        userController.deleteUser(1L);
        verify(userService).delete(1L);
    }

}
