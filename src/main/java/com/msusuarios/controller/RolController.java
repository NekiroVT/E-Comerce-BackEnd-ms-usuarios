package com.msusuarios.controller;

import com.msusuarios.dto.RolDTO;
import com.msusuarios.service.RolService;
import com.msusuarios.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    // 🔐 Crear rol
    @PostMapping
    public ResponseEntity<?> crearRol(
            @RequestBody RolDTO dto,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:roles.create")) {
            return ResponseEntity.status(403).body("No tienes permiso para crear roles");
        }

        try {
            return ResponseEntity.ok(rolService.crearRol(dto));
        } catch (RuntimeException e) {
            if ("Ya existe un rol con ese nombre".equals(e.getMessage())) {
                return ResponseEntity.status(409).body("❌ Ya existe un rol con ese nombre");
            }
            return ResponseEntity.status(500).body("❌ Error inesperado: " + e.getMessage());
        }
    }

    // 🔐 Listar todos los roles
    @GetMapping
    public ResponseEntity<?> listarRoles(
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:roles.get")) {
            return ResponseEntity.status(403).body("No tienes permiso para listar roles");
        }
        return ResponseEntity.ok(rolService.listarTodos());
    }

    // 🔐 Obtener un rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerRol(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:roles.get.id")) {
            return ResponseEntity.status(403).body("No tienes permiso para ver este rol");
        }
        try {
            UUID uuid = UUIDUtil.parseFlexibleUUID(id);
            return ResponseEntity.ok(rolService.obtenerPorId(uuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("❌ No existe el rol");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("❌ No existe el rol");
        }
    }

    // 🔐 Actualizar rol
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRol(
            @PathVariable String id,
            @RequestBody RolDTO dto,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:roles.update")) {
            return ResponseEntity.status(403).body("No tienes permiso para actualizar roles");
        }
        try {
            UUID uuid = UUIDUtil.parseFlexibleUUID(id);
            return ResponseEntity.ok(rolService.actualizarRol(uuid, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("❌ No existe el rol");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Ya existe otro rol con ese nombre")) {
                return ResponseEntity.status(409).body("❌ Ya existe otro rol con ese nombre");
            }
            return ResponseEntity.status(404).body("❌ No existe el rol");
        }
    }

    // 🔐 Eliminar rol
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:roles.delete")) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar roles");
        }
        try {
            UUID uuid = UUIDUtil.parseFlexibleUUID(id);
            return ResponseEntity.ok(rolService.eliminarRol(uuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("❌ No existe el rol");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("❌ No existe el rol");
        }
    }
}
