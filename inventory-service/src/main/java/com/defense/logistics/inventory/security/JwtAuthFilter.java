package com.defense.logistics.inventory.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final SecretKey key;
    public JwtAuthFilter(String secret) { this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                var claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(authHeader.substring(7)).getPayload();
                String role = claims.get("role", String.class);
                if (role != null) SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(claims.getSubject(), null, java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role))));
            } catch (Exception ignored) { SecurityContextHolder.clearContext(); }
        }
        filterChain.doFilter(request, response);
    }
}
