package com.msusuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String profilePhotoUrl;
    private Boolean isVerified;
    private String status;
    private List<String> roles; // ✅ AGREGAS ESTO
}
