package uk.co.dajohnston.auth.filter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JWTAuthenticationUtil {

    private static final long TOKEN_DURATION_IN_MS = MILLISECONDS.convert(7L, TimeUnit.DAYS);
    private static final String SECRET_KEY = "ThisIsASecret";

    private Clock clock;

    @Autowired
    public JWTAuthenticationUtil(Clock clock) {
        this.clock = clock;
    }

    String getToken(Authentication authenticationResult) {
        Optional<String> roleValue = authenticationResult.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst();
        String role = null;
        if (roleValue.isPresent()) {
            role = roleValue.get();
        }
        return Jwts.builder().claim("name", authenticationResult.getName()).claim("role", role)
                .setExpiration(new Date(clock.millis() + TOKEN_DURATION_IN_MS)).signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();
    }

    Authentication getAuthentication(String token) {
        Authentication result = null;
        if (token != null) {
            Claims body = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
            String user = body.get("name", String.class);
            String role = body.get("role", String.class);
            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));

            if (user != null) {
                result = new UsernamePasswordAuthenticationToken(user, null, grantedAuthorities);
            }
        }
        return result;
    }

}
