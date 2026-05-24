package com.raizesdonordeste.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Provedor de tokens JWT para autenticação.
 * 
 * Fluxo de autenticação JWT:
 * 1. O usuário envia credenciais (email/senha) no endpoint de login
 * 2. Após validação, este componente gera um token JWT assinado
 * 3. O token contém o email do usuário e seu perfil (role)
 * 4. O cliente envia o token no header Authorization: Bearer <token>
 * 5. O filtro JWT valida o token em cada requisição protegida
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /** Gera um token JWT contendo o email e o perfil do usuário */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        log.debug("Gerando token JWT para o usuário: {}", email);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /** Valida a assinatura e expiração do token */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    /** Extrai o email (subject) do token */
    public String getEmailFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    /** Extrai o perfil (role) do token */
    public String getRoleFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("role", String.class);
    }

    /** Retorna o tempo de expiração em segundos */
    public long getExpirationInSeconds() {
        return expiration / 1000;
    }
}
