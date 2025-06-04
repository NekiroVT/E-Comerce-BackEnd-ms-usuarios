package com.msusuarios.repository;

import com.msusuarios.entities.UsuarioRol;
import com.msusuarios.entities.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    boolean existsByUsuario_IdAndRole_Id(UUID usuarioId, UUID roleId);

    void deleteByUsuario_IdAndRole_Id(UUID usuarioId, UUID roleId);

    void deleteByUsuario_Id(UUID usuarioId); // 👈 ✅ este elimina todos los roles del usuario

    List<UsuarioRol> findAllByOrderByCreatedAtDesc();
}
