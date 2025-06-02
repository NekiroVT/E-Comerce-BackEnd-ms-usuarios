package com.msusuarios.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private String username;
    private String password;
    private String email; // 👈 Añade este campo
}
