package com.msusuarios.service.impl;

import com.msusuarios.entities.OtpPendiente;
import com.msusuarios.exception.OtpException;
import com.msusuarios.repository.OtpPendienteRepository;
import com.msusuarios.service.EmailService;
import com.msusuarios.service.OtpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpPendienteRepository otpRepository;
    private final EmailService emailService; // ✅ Inyectado correctamente

    public OtpServiceImpl(OtpPendienteRepository otpRepository, EmailService emailService) {
        this.otpRepository = otpRepository;
        this.emailService = emailService;
    }

    @Override
    public void enviarOtp(String email) {
        String codigo = String.format("%04d", new Random().nextInt(10000));
        OtpPendiente otp = otpRepository.findByEmail(email);

        if (otp == null) {
            otp = new OtpPendiente();
            otp.setId(UUID.randomUUID()); // 👈 NECESARIO si no usas @GeneratedValue
            otp.setEmail(email);
        }

        otp.setOtp(codigo);
        otp.setCreadoEn(LocalDateTime.now());

        otpRepository.save(otp);

        // ✅ ENVÍA EL CORREO REAL
        emailService.sendOtpEmail(email, codigo);
    }

    @Override
    @Transactional
    public void verificarOtp(String email, String otp) {
        OtpPendiente otpPendiente = otpRepository.findByEmail(email);

        if (otpPendiente == null) {
            throw new OtpException("❌ No se encontró un OTP para este correo.");
        }

        if (!otpPendiente.getOtp().equals(otp)) {
            throw new OtpException("❌ El código OTP es incorrecto.");
        }

        if (otpPendiente.getCreadoEn().isBefore(LocalDateTime.now().minusSeconds(60))) {
            otpRepository.delete(otpPendiente);
            otpRepository.flush();
            throw new OtpException("❌ El código OTP ha expirado.");
        }

        otpRepository.delete(otpPendiente);
        otpRepository.flush();
    }

    @Override
    public void reenviarOtp(String email) {
        enviarOtp(email);
    }

    @Override
    public int obtenerTiempoRestante(String email) {
        OtpPendiente otp = otpRepository.findByEmail(email);

        if (otp == null) return 0;

        long segundosPasados = java.time.Duration.between(otp.getCreadoEn(), LocalDateTime.now()).getSeconds();
            long restante = 15 - segundosPasados;

        return (int) Math.max(restante, 0);
    }
}
