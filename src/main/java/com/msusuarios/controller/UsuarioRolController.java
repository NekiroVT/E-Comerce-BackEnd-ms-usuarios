package com.msusuarios.controller;

import com.msusuarios.dto.UsuarioRolDTO;
import com.msusuarios.service.UsuarioRolService;
import com.msusuarios.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/userroles")
@RequiredArgsConstructor
public class UsuarioRolController {

    private final UsuarioRolService service;

    // 🟢 Asignar rol a un usuario
    @PostMapping
    public ResponseEntity<?> asignarRol(
            @RequestBody UsuarioRolDTO dto,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:userroles.create")) {
            return ResponseEntity.status(403).body("No tienes permiso para asignar roles");
        }

        try {
            service.asignarRolAUsuario(dto);
            return ResponseEntity.ok("Rol asignado al usuario correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }


    // 🔴 Eliminar rol de un usuario
    @DeleteMapping
    public ResponseEntity<?> eliminarRol(
            @RequestBody UsuarioRolDTO dto,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:userroles.delete")) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar roles de usuarios");
        }

        try {
            service.eliminarRolDeUsuario(dto);
            return ResponseEntity.ok("Rol eliminado del usuario correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }


    // 📋 Listar todos los roles de usuarios
    @GetMapping
    public ResponseEntity<?> listarTodos(
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:userroles.get")) {
            return ResponseEntity.status(403).body("No tienes permiso para listar los roles de los usuarios");
        }
        List<UsuarioRolDTO> lista = service.listarTodos();
        return ResponseEntity.ok(lista);
    }

    // 🔎 Listar roles de un usuario específico
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<?> listarPorUsuario(
            @PathVariable String userId,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:userroles.get.id")) {
            return ResponseEntity.status(403).body("No tienes permiso para ver los roles de este usuario");
        }

        try {
            UUID uuid = UUIDUtil.parseFlexibleUUID(userId); // ✅ acepta mayúsculas, sin guiones, etc.
            List<UsuarioRolDTO> lista = service.listarPorUsuarioId(uuid);
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ UUID inválido");
        }
    }

}
