package com.msusuarios.controller;

import com.msusuarios.dto.UsuarioResponseDTO;
import com.msusuarios.dto.UsuarioSimpleDTO;
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
}
