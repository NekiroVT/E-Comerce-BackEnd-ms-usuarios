package com.msusuarios.service;

import com.msusuarios.dto.RolePermisoDTO;

import java.util.List;
import java.util.UUID;

public interface RolePermissionService {
    void asignar(RolePermisoDTO dto);
    void eliminar(RolePermisoDTO dto);
    List<RolePermisoDTO> listar();
    List<RolePermisoDTO> listarPorRolId(UUID roleId); // 👈 CAMBIO AQUÍ
}
