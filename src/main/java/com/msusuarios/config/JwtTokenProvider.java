package com.msusuarios.config;

import com.msusuarios.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final String jwtSecret = "MiClaveSecretaSuperLargaYSeguraQueTieneAlMenos512BitsDeLongitud";
    private final long jwtExpiration = 86400000; // 1 día en milisegundos

    // ✅ Generar token con permisos y username
    public String generateToken(Usuario usuario) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        String permisos = usuario.getRoles().stream()
                .flatMap(ur -> ur.getRole().getPermisos().stream())
                .map(rp -> rp.getPermission().getName()) // Ej: productos:crud
                .distinct()
                .collect(Collectors.joining(",")); // productos:crud,ventas:ver

        return Jwts.builder()
                .setSubject(usuario.getId().toString())
                .claim("username", usuario.getUsername())
                .claim("permissions", permisos) // si tienes permisos
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Validar si el token es correcto o expirado
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ✅ Obtener el ID del usuario (subject)
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ Obtener los permisos del token
    public String getPermissionsFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("permissions");
    }

    // ✅ Obtener el username del token (opcional)
    public String getUsernameFromToken(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("username");
    }
}
