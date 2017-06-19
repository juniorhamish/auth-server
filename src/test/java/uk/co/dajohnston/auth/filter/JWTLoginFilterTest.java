package uk.co.dajohnston.auth.filter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@RunWith(MockitoJUnitRunner.class)
public class JWTLoginFilterTest {

    @Mock
    private JWTAuthenticationUtil authenticationUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private Authentication authentication;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;
    private JWTLoginFilter jwtLoginFilter;

    @Before
    public void setUp() throws Exception {
        jwtLoginFilter = new JWTLoginFilter("url", authenticationManager, authenticationUtil);
        request = mock(HttpServletRequest.class);
    }

    @Test
    public void successfulAuthenticationShouldAddHeaderToResponse() throws IOException, ServletException {
        when(authenticationUtil.getToken(authentication)).thenReturn("Token");
        jwtLoginFilter.successfulAuthentication(request, response, null, authentication);
        verify(response).addHeader("Authorization", "Bearer Token");
    }

    @Test
    public void shouldReadAccountCredentialsFromRequestAndAttemptAuthenticationWithEmailAddressAndPassword()
            throws IOException, ServletException {
        Authentication authentication = new UsernamePasswordAuthenticationToken("dave@test.com", "password");
        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        when(request.getInputStream())
                .thenReturn(new MockServletInputStream("{\"emailAddress\":\"dave@test.com\",\"password\":\"password\"}"));
        Authentication authenticationResult = jwtLoginFilter.attemptAuthentication(request, response);

        assertThat(authenticationResult, is(authentication));
    }

    private class MockServletInputStream extends ServletInputStream {

        private InputStream inputStream;
        private int totalBytes;
        private int bytesRead;

        MockServletInputStream(String string) {
            this.inputStream = new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8));
            bytesRead = 0;
            totalBytes = string.getBytes(StandardCharsets.UTF_8).length;
        }

        @Override
        public int read() throws IOException {
            bytesRead++;
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return bytesRead == totalBytes;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // Do nothing
        }
    }

}