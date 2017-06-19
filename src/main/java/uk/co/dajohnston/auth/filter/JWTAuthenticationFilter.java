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

public class JWTAuthenticationFilter extends GenericFilterBean {

    private JWTAuthenticationUtil jwtAuthenticationUtil;

    public JWTAuthenticationFilter(JWTAuthenticationUtil jwtAuthenticationUtil) {
        this.jwtAuthenticationUtil = jwtAuthenticationUtil;
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
            return jwtAuthenticationUtil.getAuthentication(authHeader.replace("Bearer ", ""));
        }
        return null;
    }
}
