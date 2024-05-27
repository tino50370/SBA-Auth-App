package io.javabrains.springsecurityjwt;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service // Marks this class as a Spring service, making it a candidate for Spring's component scanning to detect and register as a Spring bean
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // This method is overridden to provide user details for authentication
        // In a real application, this method should query a user repository (e.g., database) to retrieve user information
        // Here, we are hardcoding the user details for demonstration purposes
        
        // Creating a User object with hardcoded username ("foo"), password ("foo"), and an empty list of authorities (roles/permissions)
        return new User("foo", "foo", new ArrayList<>());
    }
}
