package com.msusuarios.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // ✅ Envío del código OTP
    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Tu código de verificación - Wimine");
            helper.setFrom("sistemas5@proactivoweb.com");

            String html = """
                    <h2>🛡️ Código de verificación</h2>
                    <p>Tu código para completar el registro es:</p>
                    <h3 style=\"color: #4CAF50;\">%s</h3>
                    <p>Este código expira en <b>1 minuto</b>.</p>
                    <hr>
                    <small>Si tú no solicitaste este código, ignora este correo.</small>
                    """.formatted(otp);

            helper.setText(html, true);
            mailSender.send(message);
            System.out.println("📩 Código OTP enviado a: " + to);
        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar código OTP: " + e.getMessage());
        }
    }
}
