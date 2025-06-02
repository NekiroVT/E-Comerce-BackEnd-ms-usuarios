package com.msusuarios.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp_pendientes") // ✅ Nombre de la tabla en plural, como tus otras tablas
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpPendiente {

    @Id
    @Column(name = "id_otp_pendientes", columnDefinition = "RAW(16)") // ✅ UUID con nombre correcto
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "otp", nullable = false)
    private String otp;

    @Column(name = "creado_en", nullable = false)
    private LocalDateTime creadoEn;

    // ✅ Se eliminó completamente jsonUsuarioTemporal
}
