package uk.co.dajohnston.auth.service;

import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import uk.co.dajohnston.auth.model.Role;
import uk.co.dajohnston.auth.model.User;
import uk.co.dajohnston.auth.repository.UserRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {

    public static final String TEST_EMAIL_ADDRESS = "test@foo.com";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    private UserDetailsServiceImpl userDetailsService;

    @Before
    public void setUp() throws Exception {
        userDetailsService = new UserDetailsServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    public void shouldThrowExceptionIfUserDoesNotExist() {
        when(userRepository.findByEmailAddress("dave@test.com")).thenReturn(null);
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(is("User with email address dave@test.com does not exist"));
        userDetailsService.loadUserByUsername("dave@test.com");
    }

    @Test
    public void shouldReturnUserWithDetailsFromLoadedUser() {
        User user = new User();
        user.setEmailAddress(TEST_EMAIL_ADDRESS);
        user.setPassword("password");
        user.setRole(Role.USER);
        when(userRepository.findByEmailAddress(TEST_EMAIL_ADDRESS)).thenReturn(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL_ADDRESS);

        assertThat(userDetails.getUsername(), is(TEST_EMAIL_ADDRESS));
        assertThat(userDetails.getPassword(), is("password"));
        assertThat(userDetails.getAuthorities(), contains(new SimpleGrantedAuthority("USER")));
    }

    @Test
    public void shouldEncryptPasswordAndSetRoleToUserBeforeSaving() {
        User user = mock(User.class);
        when(user.getPassword()).thenReturn("password");
        when(passwordEncoder.encode("password")).thenReturn("Encoded");
        userDetailsService.save(user);
        InOrder inOrder = Mockito.inOrder(user, userRepository);
        inOrder.verify(user).setPassword("Encoded");
        inOrder.verify(user).setRole(Role.USER);
        inOrder.verify(userRepository).save(user);
    }

    @Test
    public void shouldGetAllUsersFromRepository() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));
        List<User> allUsers = userDetailsService.getAllUsers();
        assertThat(allUsers, contains(user));
    }

}