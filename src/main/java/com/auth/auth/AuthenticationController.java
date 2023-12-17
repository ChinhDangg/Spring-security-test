package com.auth.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping()
    public String defaultSignIn() {
        return "signIn";
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        if (!request.haveAllFields())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthenticationResponse(Map.of("error", "BAD REQUEST")));
        AuthenticationResponse response = authenticationService.register(request);
        if (response == null)
            return ResponseEntity.ok(new AuthenticationResponse(Map.of("error", "Email already exist")));
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        if (!request.haveAllFields())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthenticationResponse(Map.of("error", "BAD REQUEST")));
        AuthenticationResponse response = authenticationService.authenticate(request);
        if (response == null)
            return ResponseEntity.ok(new AuthenticationResponse(Map.of("error", "Wrong username of password")));
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
