package com.msusuarios.service;

import com.msusuarios.dto.RolDTO;

import java.util.List;
import java.util.UUID;

public interface RolService {

    // Crea un nuevo rol (requiere permiso: usuarios:roles.create)
    RolDTO crearRol(RolDTO dto);

    // Lista todos los roles (requiere permiso: usuarios:roles.get)
    List<RolDTO> listarTodos();

    // Obtiene un rol por ID (requiere permiso: usuarios:roles.get.id)
    RolDTO obtenerPorId(UUID id);

    // Actualiza un rol (requiere permiso: usuarios:roles.update)
    RolDTO actualizarRol(UUID id, RolDTO dto);

    // Elimina un rol (requiere permiso: usuarios:roles.delete)
    String eliminarRol(UUID id);
}
