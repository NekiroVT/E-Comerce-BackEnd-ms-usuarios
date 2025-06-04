package com.msusuarios.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UsuarioRolListadoDTO {
    private UUID userId;            // 🆕 ID del usuario
    private UUID roleId;            // 🆕 ID del rol
    private String username;        // Nombre del usuario
    private String roleName;        // Nombre del rol asignado
    private LocalDateTime createdAt; // Fecha de creación de la relación
}
