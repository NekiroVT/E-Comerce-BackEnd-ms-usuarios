package com.msusuarios.controller;

import com.msusuarios.dto.RolePermisoDTO;
import com.msusuarios.service.RolePermissionService;
import com.msusuarios.util.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rolespermisos")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService service;

    // 🔐 Crear relación Rol-Permiso
    @PostMapping
    public ResponseEntity<?> asignarPermiso(
            @RequestBody RolePermisoDTO dto,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        try {
            if (permisos == null || !permisos.contains("usuarios:rolespermisos.create")) {
                return ResponseEntity.status(403).body("No tienes permiso para asignar permisos a roles");
            }

            service.asignar(dto);
            return ResponseEntity.ok("✅ Permiso asignado al rol correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("❌ UUID inválido: " + e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("ya está asignado")) {
                return ResponseEntity.status(409).body("❌ Ese permiso ya está asignado a este rol");
            }
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }

    // 🔐 Eliminar relación Rol-Permiso
    @DeleteMapping
    public ResponseEntity<?> eliminarPermiso(
            @RequestBody RolePermisoDTO dto,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        try {
            if (permisos == null || !permisos.contains("usuarios:rolespermisos.delete")) {
                return ResponseEntity.status(403).body("No tienes permiso para eliminar permisos de roles");
            }

            service.eliminar(dto);
            return ResponseEntity.ok("✅ Permiso eliminado del rol correctamente");

        } catch (RuntimeException e) {
            if (e.getMessage().contains("No existe esa relación")) {
                return ResponseEntity.status(404).body("❌ No existe esa relación rol-permiso");
            }
            return ResponseEntity.status(400).body("❌ " + e.getMessage());
        }
    }

    // 🔐 Listar TODAS las relaciones rol-permiso
    @GetMapping
    public ResponseEntity<?> listarTodo(
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:rolespermisos.get")) {
            return ResponseEntity.status(403).body("No tienes permiso para ver los roles con permisos");
        }

        List<RolePermisoDTO> lista = service.listar();
        if (lista.isEmpty()) {
            return ResponseEntity.ok("📭 No hay relaciones rol-permiso registradas");
        }

        return ResponseEntity.ok(lista);
    }

    // 🔐 Listar permisos de un rol específico
    @GetMapping("/rol/{roleId}")
    public ResponseEntity<?> listarPorRol(
            @PathVariable String roleId,
            @RequestHeader(value = "X-User-Permissions", required = false) String permisos
    ) {
        if (permisos == null || !permisos.contains("usuarios:rolespermisos.get.id")) {
            return ResponseEntity.status(403).body("No tienes permiso para ver los permisos de este rol");
        }

        try {
            UUID uuid = UUIDUtil.parseFlexibleUUID(roleId); // <-- Aquí puede lanzar IllegalArgumentException
            List<RolePermisoDTO> lista = service.listarPorRolId(uuid);

            if (lista.isEmpty()) {
                return ResponseEntity.ok("⚠️ Este rol no tiene permisos asignados");
            }

            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException e) {
            // 👇 Esta excepción se lanza si UUID está mal formado o es muy largo
            return ResponseEntity.status(404).body("❌ Rol no encontrado");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("❌ Rol no encontrado");
        }
    }

}