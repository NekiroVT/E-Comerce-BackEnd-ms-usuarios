package com.msusuarios.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @Column(name = "id_roles", columnDefinition = "RAW(16)")
    private UUID id;

    private String name;
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioRol> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermiso> permisos = new ArrayList<>();

    // Constructor personalizado (usado en RolServiceImpl)
    public Role(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now(); // 💾 Registra fecha actual al crear
        this.usuarios = new ArrayList<>();
        this.permisos = new ArrayList<>();
    }
}
