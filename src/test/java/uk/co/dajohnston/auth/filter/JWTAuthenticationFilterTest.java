package uk.co.dajohnston.auth.filter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class JWTAuthenticationFilterTest {

    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private FilterChain filterChain;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private JWTAuthenticationUtil jwtAuthenticationUtil;
    @Mock
    private Authentication authentication;
    @Mock
    private ServletResponse servletResponse;
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Before
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtAuthenticationUtil);
    }

    @Test
    public void shouldSetAuthenticationToNullIfHeaderIsNotPresent() throws IOException, ServletException {
        when(servletRequest.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilter(servletRequest, null, filterChain);

        verify(securityContext).setAuthentication(null);
    }

    @Test
    public void shouldExtractTokenFromHeaderToGetAuthentication() throws IOException, ServletException {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer Blah");
        when(jwtAuthenticationUtil.getAuthentication("Blah")).thenReturn(authentication);

        jwtAuthenticationFilter.doFilter(servletRequest, null, filterChain);

        verify(securityContext).setAuthentication(authentication);
    }

    @Test
    public void shouldCallTheFilterChainAfterSettingTheAuthenticationInTheSecurityContext() throws IOException, ServletException {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer Blah");
        when(jwtAuthenticationUtil.getAuthentication("Blah")).thenReturn(authentication);

        jwtAuthenticationFilter.doFilter(servletRequest, servletResponse, filterChain);

        InOrder inOrder = Mockito.inOrder(securityContext, filterChain);
        inOrder.verify(securityContext).setAuthentication(authentication);
        inOrder.verify(filterChain).doFilter(servletRequest, servletResponse);
    }
}
