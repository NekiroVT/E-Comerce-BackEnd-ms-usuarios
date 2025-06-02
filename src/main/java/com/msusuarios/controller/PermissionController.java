package com.msusuarios.controller;

import com.msusuarios.dto.PermissionDTO;
import com.msusuarios.service.PermissionService;
import com.msusuarios.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PermissionDTO dto,
                                    @RequestHeader(value = "X-User-Permissions", required = false) String permisos) {
        if (permisos == null || !permisos.contains("usuarios:permisos.create")) {
            return ResponseEntity.status(403).body("No tienes permiso para crear permisos");
        }

        try {
            return ResponseEntity.ok(permissionService.create(dto));
        } catch (RuntimeException e) {
            if ("Ya existe un permiso con ese nombre".equals(e.getMessage())) {
                return ResponseEntity.status(409).body(e.getMessage());
            }
            return ResponseEntity.status(500).body("Error al crear el permiso");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestHeader(value = "X-User-Permissions", required = false) String permisos) {
        if (permisos == null || !permisos.contains("usuarios:permisos.get")) {
            return ResponseEntity.status(403).body("No tienes permiso para listar permisos");
        }
        return ResponseEntity.ok(permissionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id,
                                     @RequestHeader(value = "X-User-Permissions", required = false) String permisos) {
        if (permisos == null || !permisos.contains("usuarios:permisos.get.id")) {
            return ResponseEntity.status(403).body("No tienes permiso para ver este permiso");
        }

        try {
            UUID uuid = UUIDUtil.parseFlexibleUUID(id);
            return ResponseEntity.ok(permissionService.getById(uuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("❌ No existe el permiso");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @RequestBody PermissionDTO dto,
                                    @RequestHeader(value = "X-User-Permissions", required = false) String permisos) {
        if (permisos == null || !permisos.contains("usuarios:permisos.update")) {
            return ResponseEntity.status(403).body("No tienes permiso para actualizar permisos");
        }

        try {
            UUID uuid = UUIDUtil.parseFlexibleUUID(id);
            return ResponseEntity.ok(permissionService.update(uuid, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("❌ No existe el permiso");
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Ya existe otro permiso con ese nombre")) {
                return ResponseEntity.status(409).body("❌ Ya existe otro permiso con ese nombre");
            } else if (e.getMessage().equals("Permiso no encontrado")) {
                return ResponseEntity.status(404).body("❌ No existe el permiso");
            } else {
                return ResponseEntity.status(500).body("❌ Error inesperado: " + e.getMessage());
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id,
                                    @RequestHeader(value = "X-User-Permissions", required = false) String permisos) {
        if (permisos == null || !permisos.contains("usuarios:permisos.delete")) {
            return ResponseEntity.status(403).body("No tienes permiso para eliminar permisos");
        }

        try {
            UUID uuid = UUIDUtil.parseFlexibleUUID(id);
            return ResponseEntity.ok(permissionService.delete(uuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("❌ No existe el permiso");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("❌ No se puede hacer estó");
        }
    }
}
