package com.msusuarios.service;

public interface OtpService {

    void enviarOtp(String email);   // 📨 Envía el OTP, crea o actualiza el existente

    void verificarOtp(String email, String otp); // ✅ Verifica el OTP (ahora no devuelve boolean, lanza excepción si falla)

    void reenviarOtp(String email); // 🔁 Reenvía nuevo OTP si aún tiene uno existente

    int obtenerTiempoRestante(String email); // ⏳ Devuelve segundos restantes
}
