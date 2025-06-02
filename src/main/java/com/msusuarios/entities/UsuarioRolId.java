package com.msusuarios.entities;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRolId implements Serializable {
    private UUID usuario;
    private UUID role;
}
