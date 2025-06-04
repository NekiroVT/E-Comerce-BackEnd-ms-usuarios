package com.msusuarios.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioUpdateDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String profilePhotoUrl;
    private String status;
}
