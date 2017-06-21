package uk.co.dajohnston.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uk.co.dajohnston.auth.filter.JsonWebTokenAuthenticationFilter;
import uk.co.dajohnston.auth.filter.JsonWebTokenAuthenticationUtil;
import uk.co.dajohnston.auth.filter.JsonWebTokenLoginFilter;
import uk.co.dajohnston.auth.model.Role;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JsonWebTokenAuthenticationUtil jsonWebTokenAuthenticationUtil;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder, JsonWebTokenAuthenticationUtil
            jsonWebTokenAuthenticationUtil) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jsonWebTokenAuthenticationUtil = jsonWebTokenAuthenticationUtil;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.POST, "/login", "/signup").permitAll().antMatchers("/users")
                .hasRole(Role.ADMIN.name()).anyRequest().authenticated().and().addFilterBefore(new JsonWebTokenLoginFilter("/login",
                authenticationManager(), jsonWebTokenAuthenticationUtil), UsernamePasswordAuthenticationFilter.class).addFilterBefore(new
                JsonWebTokenAuthenticationFilter(jsonWebTokenAuthenticationUtil), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
