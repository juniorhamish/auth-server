package uk.co.dajohnston.auth.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class JsonWebTokenAuthenticationFilter extends GenericFilterBean {

    private final JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil;

    public JsonWebTokenAuthenticationFilter(JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil) {
        this.jsonWebTokenAuthenticationUtil = jsonWebTokenAuthenticationUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Authentication authentication = getAuthentication((HttpServletRequest) request);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null) {
            return jsonWebTokenAuthenticationUtil.getAuthentication(authHeader.replace("Bearer ", ""));
        }
        return null;
    }
}
