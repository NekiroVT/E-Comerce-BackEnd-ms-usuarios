package com.msusuarios.repository;

import com.msusuarios.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    // Buscar rol por nombre exacto
    Optional<Role> findByName(String name);

    // Validar si ya existe un rol con ese nombre
    boolean existsByName(String name);

    // Obtener todos los roles ordenados por fecha (más recientes primero)
    List<Role> findAllByOrderByCreatedAtDesc();
}
