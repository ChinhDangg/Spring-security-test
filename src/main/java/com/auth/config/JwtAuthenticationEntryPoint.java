package com.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.addCookie(removeAuthCookie());
        String redirectUrl = (request.getRequestURI().equals("/authentication")) ? "" : ("?r=" + request.getRequestURI());
        response.sendRedirect("/authentication"+redirectUrl);
    }

    private Cookie removeAuthCookie() {
        Cookie cookie = new Cookie("Auth", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}
