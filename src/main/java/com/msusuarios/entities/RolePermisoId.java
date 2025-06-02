package com.msusuarios.entities;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermisoId implements Serializable {
    private UUID role;
    private UUID permission;
}
