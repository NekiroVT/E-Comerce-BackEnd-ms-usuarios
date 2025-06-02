package com.msusuarios.service;

import com.msusuarios.dto.UsuarioRolDTO;

import java.util.List;
import java.util.UUID;

public interface UsuarioRolService {

    void asignarRolAUsuario(UsuarioRolDTO dto);

    /**
     * Elimina un rol del usuario con reglas:
     * - Si el rol a eliminar es CLIENTE y es el único, no se elimina.
     * - Si el usuario se queda sin roles, se le asigna automáticamente el rol CLIENTE.
     */
    void eliminarRolDeUsuario(UsuarioRolDTO dto);

    List<UsuarioRolDTO> listarTodos();

    List<UsuarioRolDTO> listarPorUsuarioId(UUID usuarioId);
}
