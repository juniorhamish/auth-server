package uk.co.dajohnston.auth.filter;

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
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JsonWebTokenAuthenticationFilterTest {

    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private FilterChain filterChain;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil;
    @Mock
    private Authentication authentication;
    @Mock
    private ServletResponse servletResponse;
    private JsonWebTokenAuthenticationFilter jsonWebTokenAuthenticationFilter;

    @Before
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
        jsonWebTokenAuthenticationFilter = new JsonWebTokenAuthenticationFilter(jsonWebTokenAuthenticationUtil);
    }

    @Test
    public void shouldSetAuthenticationToNullIfHeaderIsNotPresent() throws IOException, ServletException {
        when(servletRequest.getHeader("Authorization")).thenReturn(null);

        jsonWebTokenAuthenticationFilter.doFilter(servletRequest, null, filterChain);

        verify(securityContext).setAuthentication(null);
    }

    @Test
    public void shouldExtractTokenFromHeaderToGetAuthentication() throws IOException, ServletException {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer Blah");
        when(jsonWebTokenAuthenticationUtil.getAuthentication("Blah")).thenReturn(authentication);

        jsonWebTokenAuthenticationFilter.doFilter(servletRequest, null, filterChain);

        verify(securityContext).setAuthentication(authentication);
    }

    @Test
    public void shouldCallTheFilterChainAfterSettingTheAuthenticationInTheSecurityContext() throws IOException, ServletException {
        when(servletRequest.getHeader("Authorization")).thenReturn("Bearer Blah");
        when(jsonWebTokenAuthenticationUtil.getAuthentication("Blah")).thenReturn(authentication);

        jsonWebTokenAuthenticationFilter.doFilter(servletRequest, servletResponse, filterChain);

        InOrder inOrder = Mockito.inOrder(securityContext, filterChain);
        inOrder.verify(securityContext).setAuthentication(authentication);
        inOrder.verify(filterChain).doFilter(servletRequest, servletResponse);
    }
}
