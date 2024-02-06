package com.auth.auth;

import com.auth.config.JwtService;
import com.auth.user.Role;
import com.auth.user.User;
import com.auth.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        String email = request.getEmail();
        //email must not already be registered
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = User.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(email)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            return AuthenticationResponse.builder()
                    .cookie(makeAuthenticateCookie(user))
                    .build();
        }
        return null;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            String userEmail = request.getEmail();
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userEmail, request.getPassword())
            );
            //if authenticate above fails, the following code will not trigger
            //check the ApplicationConfig for authentication details
            var user = userRepository.findByEmail(userEmail).orElseThrow();
            return AuthenticationResponse.builder()
                    .cookie(makeAuthenticateCookie(user))
                    .build();
        } catch (BadCredentialsException e) {
            System.out.println("Wrong password or username");
        }
        return null;
    }

    public Cookie makeAuthenticateCookie(UserDetails user) {
        Date cookieMaxTime = new Date(System.currentTimeMillis() + (1000 * 3600));
        Map<String, Object> c = Map.of("cookieMaxTime", cookieMaxTime);
        String jwtToken = jwtService.generateToken(c, user);
        Cookie cookie = new Cookie("Auth", jwtToken);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        return cookie;
    }
}
