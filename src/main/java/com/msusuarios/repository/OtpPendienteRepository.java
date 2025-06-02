package com.msusuarios.repository;

import com.msusuarios.entities.OtpPendiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OtpPendienteRepository extends JpaRepository<OtpPendiente, UUID> {

    // 🧼 Elimina todos los OTPs creados antes de la fecha límite y devuelve cuántos eliminó
    int deleteAllByCreadoEnBefore(LocalDateTime fechaLimite);

    // 🧪 Buscar un OTP por correo
    OtpPendiente findByEmail(String email);
}
