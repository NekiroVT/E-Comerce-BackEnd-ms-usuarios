package com.msusuarios.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions")
@IdClass(RolePermisoId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermiso {

    @Id
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Id
    @ManyToOne
    @JoinColumn(name = "perm_id")
    private Permission permission;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public RolePermiso(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
        this.createdAt = LocalDateTime.now(); // ✅ opcional pero útil
    }
}
