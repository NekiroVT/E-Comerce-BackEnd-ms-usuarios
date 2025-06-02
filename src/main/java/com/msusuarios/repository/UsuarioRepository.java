package com.msusuarios.repository;

import com.msusuarios.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmailIgnoreCase(String email); // ✅ forma segura
    boolean existsByUsername(String username);
    boolean existsByEmailIgnoreCase(String email); // ✅ opcional si validas antes de registrar
}
