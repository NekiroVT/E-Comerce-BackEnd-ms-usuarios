package com.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UsuarioSimpleDTO {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String profilePhotoUrl;
    private Boolean isVerified;
    private String status;
}
