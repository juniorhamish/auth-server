package uk.co.dajohnston.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Clock;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JsonWebTokenAuthenticationUtilTest {

    private static final String SIGNING_KEY = "Tn5UViRLPEgpI0hvbmZRbjpmTlY5RzJpJys+STxfImp7X203KGMsdCY5ZSpfYmNUTi0nQVUoVj9eZXw3SDNv";
    @Mock
    private Authentication authenticationResult;
    @Mock
    private Clock clock;
    private JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil;

    @Before
    public void setUp() {
        jsonWebTokenAuthenticationUtil = new JsonWebTokenAuthenticationUtil(clock);
        when(clock.millis()).thenReturn(System.currentTimeMillis());
    }

    @Test
    public void shouldSetNameInTokenToAuthResultName() {
        when(authenticationResult.getName()).thenReturn("Dave");
        String token = jsonWebTokenAuthenticationUtil.getToken(authenticationResult);

        assertThat(token, hasClaim("name", "Dave", String.class));
    }

    @Test
    public void shouldSetRoleInTokenFromGrantedAuthoritiesInAuthenticationResult() {
        doReturn(singletonList(new SimpleGrantedAuthority("ADMIN"))).when(authenticationResult).getAuthorities();
        String token = jsonWebTokenAuthenticationUtil.getToken(authenticationResult);

        assertThat(token, hasClaim("role", "ADMIN", String.class));
    }

    @Test
    public void shouldSetRoleToNullInTokenIfNoGrantedAuthoritiesInAuthenticationResult() {
        doReturn(emptyList()).when(authenticationResult).getAuthorities();
        String token = jsonWebTokenAuthenticationUtil.getToken(authenticationResult);

        assertThat(token, hasClaim("role", null, String.class));
    }

    @Test
    public void shouldSetRoleToFirstGrantedAuthorityIfMultipleGrantedAuthoritiesInAuthenticationResult() {
        doReturn(asList(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN"))).when(authenticationResult)
                .getAuthorities();
        String token = jsonWebTokenAuthenticationUtil.getToken(authenticationResult);

        assertThat(token, hasClaim("role", "USER", String.class));
    }

    @Test
    public void shouldSetExpiryToSevenDaysTime() {
        when(clock.millis()).thenReturn(9999000000000L);
        String token = jsonWebTokenAuthenticationUtil.getToken(authenticationResult);

        Date expiry = new Date(clock.millis() + MILLISECONDS.convert(7L, TimeUnit.DAYS));
        assertThat(token, hasClaim("exp", expiry, Date.class));
    }

    @Test
    public void authenticationShouldBeNullIfTokenIsNull() {
        Authentication authentication = jsonWebTokenAuthenticationUtil.getAuthentication(null);

        assertThat(authentication, is(nullValue()));
    }

    @Test
    public void authenticationShouldBeNullIfNameClaimIsNotSet() {
        String jwt = Jwts.builder().claim("Foo", "Bar").signWith(SignatureAlgorithm.HS512, SIGNING_KEY).compact();
        Authentication authentication = jsonWebTokenAuthenticationUtil.getAuthentication(jwt);

        assertThat(authentication, is(nullValue()));
    }

    @Test
    public void authenticationShouldGetUserNameFromToken() {
        String jwt = Jwts.builder().claim("name", "Dave").signWith(SignatureAlgorithm.HS512, SIGNING_KEY).compact();
        Authentication authentication = jsonWebTokenAuthenticationUtil.getAuthentication(jwt);
        assertThat(authentication.getName(), is("Dave"));
    }

    @Test
    public void authenticationShouldHoldGrantedAuthorityForTheRoleClaim() {
        String jwt = Jwts.builder().claim("role", "ADMIN").claim("name", "Dave").signWith(SignatureAlgorithm.HS512, SIGNING_KEY).compact();
        Authentication authentication = jsonWebTokenAuthenticationUtil.getAuthentication(jwt);
        assertThat(authentication.getAuthorities(), contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private <T> Matcher<String> hasClaim(String field, T value, Class<T> valueClass) {
        return new BaseMatcher<String>() {

            @Override
            public boolean matches(Object item) {
                return getClaim(item.toString()) == null ? value == null : getClaim(item.toString()).equals(value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(field + " = " + value);
            }

            @Override
            public void describeMismatch(Object item, Description description) {
                description.appendValue(field + " = " + getClaim(item.toString()));
            }

            private T getClaim(String token) {
                Claims body = Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
                return body.get(field, valueClass);
            }
        };
    }
}
