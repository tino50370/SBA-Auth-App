package io.javabrains.springsecurityjwt;

import io.javabrains.springsecurityjwt.filters.JwtRequestFilter;
import io.javabrains.springsecurityjwt.models.AuthenticationRequest;
import io.javabrains.springsecurityjwt.models.AuthenticationResponse;
import io.javabrains.springsecurityjwt.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication // Annotation to mark this class as a Spring Boot application
public class SpringSecurityJwtApplication {

    public static void main(String[] args) {
        // Launch the Spring Boot application
        SpringApplication.run(SpringSecurityJwtApplication.class, args);
    }

}

@RestController // Marks this class as a controller where every method returns a domain object instead of a view
class HelloWorldController {

    @Autowired // Dependency injection for AuthenticationManager
    private AuthenticationManager authenticationManager;

    @Autowired // Dependency injection for JwtUtil
    private JwtUtil jwtTokenUtil;

    @Autowired // Dependency injection for MyUserDetailsService
    private MyUserDetailsService userDetailsService;

    @RequestMapping({ "/hello" }) // Map the /hello endpoint to this method
    public String firstPage() {
        return "Hello World"; // Returns a simple message for the /hello endpoint
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST) // Map the /authenticate endpoint to this method
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            // Authenticate the user with the provided username and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            // Throw an exception if the credentials are invalid
            throw new Exception("Incorrect username or password", e);
        }

        // Load user details from the UserDetailsService
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        // Generate a JWT token for the authenticated user
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        // Return the generated JWT token in the response
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

}

@EnableWebSecurity // Enable Spring Security's web security support
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired // Dependency injection for UserDetailsService
    private UserDetailsService myUserDetailsService;

    @Autowired // Dependency injection for JwtRequestFilter
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Configure AuthenticationManager to use the custom UserDetailsService
        auth.userDetailsService(myUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Define a password encoder (no-op for simplicity)
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // Expose the AuthenticationManager as a Spring bean
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // Configure HTTP security
        httpSecurity.csrf().disable() // Disable CSRF protection
                .authorizeRequests().antMatchers("/authenticate").permitAll() // Permit all requests to /authenticate
                .anyRequest().authenticated() // Require authentication for any other request
                .and()
                .exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Do not use session-based authentication

        // Add the JWT filter before the UsernamePasswordAuthenticationFilter
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
