package com.msusuarios.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "id_users", columnDefinition = "RAW(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl; // Nueva forma, URL o ruta del archivo

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(nullable = false)
    private String status = "pending";

    // Relación con roles
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioRol> roles;
}
