package com.msusuarios.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RolDTO {
    private UUID id;              // Sirve para editar, buscar, eliminar
    private String name;          // Nombre del rol: ADMIN, CLIENTE, etc.
    private String description;   // Descripción visible del rol
    private LocalDateTime createdAt; // Fecha de creación (solo para orden)
}
