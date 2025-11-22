package com.leavems.leave_management_system.security;

import com.leavems.leave_management_system.service.JwtService;
import com.leavems.leave_management_system.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Jws<Claims> claims = jwtService.parseToken(token);
                String email = claims.getBody().getSubject();
                String role = claims.getBody().get("role", String.class);
                Long userId = claims.getBody().get("userId", Long.class);
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                var principal = new org.springframework.security.core.userdetails.User(email, "", authorities);
                var authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                // you can also store userId in request attribute if needed
                request.setAttribute("userId", userId);
            } catch (Exception ex) {
                // invalid token -> ignore, security will reject protected endpoints
            }
        }
        filterChain.doFilter(request, response);
    }
}
