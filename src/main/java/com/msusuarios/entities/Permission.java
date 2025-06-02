package com.msusuarios.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @Column(name = "id_permissions", columnDefinition = "RAW(16)")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "permission")
    private List<RolePermiso> roles = new ArrayList<>();

    // ✅ Constructor personalizado sin lista de roles
    public Permission(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
