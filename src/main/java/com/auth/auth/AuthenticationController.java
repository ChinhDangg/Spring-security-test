package com.auth.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Random;

@Controller
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping()
    public String defaultSignIn() {
        return "signIn";
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) {
        if (!registerRequest.haveAllFields())
            response.setStatus(400);
        AuthenticationResponse authResponse = authenticationService.register(registerRequest);
        if (authResponse == null)
            response.setStatus(409);
        else {
            response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' https://ajax.googleapis.com;");
            response.setHeader("Location", "/home");
            response.addCookie(authResponse.getCookie());
            response.setStatus(200);
        }
    }

    @PostMapping("/authenticate")
    public void authenticate(@RequestBody AuthenticationRequest authRequest, HttpServletResponse response) throws IOException {
        if (!authRequest.haveAllFields())
            response.setStatus(400); // Bad Request
        AuthenticationResponse authResponse = authenticationService.authenticate(authRequest);
        if (authResponse == null)
            response.setStatus(401); // Unauthorized
        else {
            // allow from source from self and google ajax api for jquery only
            response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' https://ajax.googleapis.com;");
            response.addCookie(authResponse.getCookie());
            response.setHeader("Location", "/home");
            response.setStatus(200);
        }
    }
}
