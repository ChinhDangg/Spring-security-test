package com.auth.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping()
    public String defaultSignIn(HttpServletRequest request, HttpSession session) {
        SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (savedRequest != null) {
            String originalUrl = savedRequest.getRedirectUrl();
            System.out.println(originalUrl);
        }
        return "signIn";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (!request.haveAllFields())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("BAD REQUEST");
        AuthenticationResponse response = authenticationService.register(request);
        if (response == null)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("UNAUTHORIZED ACCESS");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse servletResponse, HttpServletRequest httpRequest) {
        if (!request.haveAllFields())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("BAD REQUEST");
        AuthenticationResponse response = authenticationService.authenticate(request);
        if (response == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("UNAUTHORIZED ACCESS");
        //allow from source from self and google ajax api for jquery only
        servletResponse.addHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' https://ajax.googleapis.com;");
        return ResponseEntity.ok(response);
    }

    private Cookie makeRefreshTokenCookie(String userEmail) {
        Random r = new Random();
        String cId = "u" + (r.nextInt() * 1000);
        Cookie cookie = new Cookie(cId, userEmail);
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);
        return cookie;
    }
}
