package com.msusuarios.controller;

import com.msusuarios.config.JwtTokenProvider;
import com.msusuarios.dto.*;
import com.msusuarios.entities.Usuario;
import com.msusuarios.repository.UsuarioRepository;
import com.msusuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;

    // 🔐 Listar todos los usuarios (simple, sin roles)
    @GetMapping
    public ResponseEntity<?> listarUsuarios(
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:usuarios.get")) {
            return ResponseEntity.status(403).body("❌ No tienes permiso para listar usuarios");
        }

        return ResponseEntity.ok(usuarioService.listarTodos()); // 👈 usar este
    }

    // 🔐 Listar usuarios simplificado (username, email, status)
    @GetMapping("/listado-simple")
    public ResponseEntity<?> listarUsuariosSimple(
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:usuarios.get")) {
            return ResponseEntity.status(403).body("❌ No tienes permiso para listar usuarios");
        }

        return ResponseEntity.ok(usuarioService.listarUsuariosSimple());
    }



    // 🔐 Obtener usuario por ID (detallado con roles)
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:usuarios.get.id")) {
            return ResponseEntity.status(403).body("❌ No tienes permiso para ver este usuario");
        }

        try {
            UsuarioResponseDTO usuario = usuarioService.obtenerPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("❌ No existe el usuario");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:usuarios.delete")) {
            return ResponseEntity.status(403).body("❌ No tienes permiso para eliminar usuarios");
        }

        try {
            usuarioService.eliminarUsuarioPorId(id);
            return ResponseEntity.ok("✅ Usuario eliminado con sus relaciones");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error al eliminar usuario: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable UUID id,
            @RequestBody UsuarioUpdateDTO dto,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:usuarios.update")) {
            return ResponseEntity.status(403).body("❌ No tienes permiso para actualizar usuarios");
        }

        return usuarioService.actualizarUsuario(id, dto);
    }

    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            @PathVariable UUID id,
            @RequestBody CambiarPasswordDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("❌ Token no enviado");
        }

        String token = authHeader.substring(7);
        String userIdToken = jwtTokenProvider.getUserIdFromToken(token);

        if (!id.toString().equals(userIdToken)) {
            return ResponseEntity.status(403).body("❌ No puedes cambiar la contraseña de otro usuario");
        }

        return usuarioService.cambiarPassword(id, dto);
    }


    @PutMapping("/{id}/cambiar-password-bypass")
    public ResponseEntity<?> cambiarPasswordComoAdmin(
            @PathVariable UUID id,
            @RequestBody CambiarPasswordDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("❌ Token no enviado");
        }

        if (permisos == null || !permisos.contains("usuarios:usuarios.update.bypassword")) {
            return ResponseEntity.status(403).body("❌ No tienes permiso para cambiar la contraseña de otro usuario");
        }

        return usuarioService.cambiarPassword(id, dto);
    }

    // ✅ Nuevo endpoint usado por ms-productos para obtener nombre del usuario






















}
