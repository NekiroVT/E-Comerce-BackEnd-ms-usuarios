package com.msusuarios.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
@IdClass(UsuarioRolId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario usuario;

    @Id
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public UsuarioRol(Usuario usuario, Role role) {
        this.usuario = usuario;
        this.role = role;
        this.createdAt = LocalDateTime.now(); // ✅ Esto es necesario
    }
}

