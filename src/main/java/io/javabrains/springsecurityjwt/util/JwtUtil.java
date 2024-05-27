package io.javabrains.springsecurityjwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service // Marks this class as a Spring service, making it a candidate for Spring's component scanning
public class JwtUtil {

    // Secret key used for signing and verifying JWT tokens
    private String SECRET_KEY = "secret";

    // Extracts the username (subject) from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extracts the expiration date from the JWT token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extracts a specific claim from the JWT token using a claims resolver function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // Extract all claims from the token
        return claimsResolver.apply(claims); // Apply the resolver function to the claims
    }

    // Extracts all claims from the JWT token
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // Checks if the JWT token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generates a JWT token for the given UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(); // Additional claims can be added to the token here
        return createToken(claims, userDetails.getUsername()); // Create the token with the username as the subject
    }

    // Creates a JWT token with the given claims and subject (username)
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())) // Set token issuance time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Set token expiration time (10 hours)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact(); // Sign the token with the HS256 algorithm and secret key
    }

    // Validates the JWT token by checking the username and expiration
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token); // Extract the username from the token
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)); // Check if the token username matches and if it is not expired
    }
}
