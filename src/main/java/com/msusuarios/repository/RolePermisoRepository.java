package com.msusuarios.repository;

import com.msusuarios.entities.RolePermiso;
import com.msusuarios.entities.RolePermisoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermisoRepository extends JpaRepository<RolePermiso, RolePermisoId> {

    boolean existsByRole_IdAndPermission_Id(UUID roleId, UUID permissionId);

    void deleteByRole_IdAndPermission_Id(UUID roleId, UUID permissionId);

    List<RolePermiso> findAllByOrderByCreatedAtDesc(); // 🆗
}
