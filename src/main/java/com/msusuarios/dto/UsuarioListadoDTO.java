
package com.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UsuarioListadoDTO {
    private UUID id;
    private String username;
    private String email;
    private String status;
}
