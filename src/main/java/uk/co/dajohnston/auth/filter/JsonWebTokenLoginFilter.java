package uk.co.dajohnston.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import uk.co.dajohnston.auth.model.AccountCredentials;

public class JsonWebTokenLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil;

    public JsonWebTokenLoginFilter(String url, AuthenticationManager authenticationManager, JsonWebTokenAuthenticationUtil
            jsonWebTokenAuthenticationUtil) {
        super(new AntPathRequestMatcher(url));
        this.jsonWebTokenAuthenticationUtil = jsonWebTokenAuthenticationUtil;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException,
            ServletException {
        AccountCredentials credentials = new ObjectMapper().readValue(request.getInputStream(), AccountCredentials.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(credentials.getEmailAddress(), credentials
                .getPassword()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication
            authResult) throws IOException, ServletException {
        String token = jsonWebTokenAuthenticationUtil.getToken(authResult);
        response.addHeader("Authorization", "Bearer " + token);
    }
}
