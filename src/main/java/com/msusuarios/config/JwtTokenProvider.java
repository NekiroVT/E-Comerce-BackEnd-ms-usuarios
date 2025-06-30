package com.msusuarios.config;

import com.msusuarios.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final String jwtSecret = "MiClaveSecretaSuperLargaYSeguraQueTieneAlMenos512BitsDeLongitud";

    private final long jwtExpiration = 1000 * 15000;

    private final long refreshTokenExpiration = 1000 * 20000;



    // 🔐 Generar clave para firmar
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ✅ Generar accessToken con permisos y username
    public String generateToken(Usuario usuario) {
        String permisos = usuario.getRoles().stream()
                .flatMap(ur -> ur.getRole().getPermisos().stream())
                .map(rp -> rp.getPermission().getName()) // Ej: productos:crud
                .distinct()
                .collect(Collectors.joining(",")); // Ej: productos:crud,ventas:ver

        return Jwts.builder()
                .setSubject(usuario.getId().toString())
                .claim("username", usuario.getUsername())
                .claim("permissions", permisos)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Generar refreshToken simple (solo con userId y tipo)
    public String generateRefreshToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getId().toString())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Validar si el token es válido y no expiró
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Obtener ID del usuario (UUID como string)
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ Obtener permisos como string separados por coma
    public String getPermissionsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("permissions", String.class);
    }

    // ✅ Obtener username del token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username", String.class);
    }
}
