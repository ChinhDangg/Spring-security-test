package com.auth.config;

import com.auth.auth.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String headerStarter = "Bearer ";
        Cookie[] cookies = request.getCookies();

        // if Authorization header doesn't have Bearer token, and have cookie with Auth name,
        // then fail the jwt filter
        if (authHeader == null || !authHeader.startsWith(headerStarter)) {
            if (cookies == null || checkCookiesHaveName(cookies, "Auth") == -1) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String jwtToken = (cookies != null) ? cookies[0].getValue() : authHeader.substring(headerStarter.length());
        final String userEmail = jwtService.extractUsername(jwtToken);
        boolean tokenValid = false;
        // every request will have different SecurityContextHolder, will always be null initially,
        // but still check to avoid same thread authentication (next filter)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                System.out.println("jwt is valid");
                System.out.println("checking jwt");
                System.out.println("check jwt token: " + jwtToken);
                System.out.println("check email: " + userEmail);
                System.out.println("check cookies: " + (cookies != null));
                tokenValid = true;
               setDetailInSecurityContextHolder(userDetails, request);
            }
        }
        // cookie is still valid but the jwt inside has expired as it has shorter duration
        // still authenticate the user but update the jwt with a new one and new cookie time
        if (!tokenValid && cookies != null && checkCookiesHaveName(cookies, "Auth") != -1) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValidAndExpiredWithin(jwtToken, userDetails)) {
                System.out.println("cookies is valid but not the jwt");
                System.out.println("check cookies coo: " + cookies[0].getName());
                System.out.println("checking jwt through cookies");
                System.out.println("check jwt token coo: " + jwtToken);
                System.out.println("check email coo: " + userEmail);
                tokenValid = true;
                setDetailInSecurityContextHolder(userDetails, request);
                response.addCookie(makeAuthenticateCookie(userDetails));
            }
        }
        if (tokenValid) {
            // if request for authentication while still having valid token, return back to home page
            if (request.getRequestURI().equals("/authentication"))
                response.sendRedirect("/home");
        }
        else
            throw new BadCredentialsException("Invalid JWT token");
        filterChain.doFilter(request, response);
    }

    private int checkCookiesHaveName(Cookie[] cookies, String cookieName) {
        for (int j = 0; j < cookies.length; j++)
            if (cookies[j].getName().equals(cookieName))
                return j;
        return -1;
    }

    private void setDetailInSecurityContextHolder(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private Cookie makeAuthenticateCookie(UserDetails user) {
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
