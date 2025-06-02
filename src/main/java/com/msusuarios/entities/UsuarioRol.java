package com.msusuarios.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "user_roles")
@IdClass(UsuarioRolId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario usuario;

    @Id
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
