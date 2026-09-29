package com.petproject.ecommerce.config;

import com.petproject.ecommerce.auth.service.RevokedTokenService;
import com.petproject.ecommerce.handler.SecurityErrorHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityErrorHandler securityErrorHandler;
    private final RevokedTokenService revokedTokenService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, SecurityErrorHandler securityErrorHandler, RevokedTokenService revokedTokenService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityErrorHandler = securityErrorHandler;
        this.revokedTokenService = revokedTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(
                request.getHeader("Authorization")
        );

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (revokedTokenService.isRevoked(token)) {
            securityErrorHandler.writeError(response, 401, "Invalid or expired token");
            return;
        }

        try {
            Claims claims = jwtTokenProvider.getClaims(token);

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            securityErrorHandler.writeError(response, 401, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}