package com.msusuarios.repository;

import com.msusuarios.entities.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
    List<Permission> findAllByOrderByCreatedAtDesc();


    String name(String name);
}
