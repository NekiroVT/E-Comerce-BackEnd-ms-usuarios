package com.msusuarios.entities;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
    @EqualsAndHashCode.Include
    private Role role;

    @Id
    @ManyToOne
    @JoinColumn(name = "perm_id")
    @EqualsAndHashCode.Include
    private Permission permission;
}

