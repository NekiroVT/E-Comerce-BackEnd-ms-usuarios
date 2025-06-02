package com.msusuarios.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Map<String, Object>> handleLoginException(LoginException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(RegistroException.class)
    public ResponseEntity<Map<String, String>> handleRegistroException(RegistroException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(409).body(error); // Conflicto
    }

    @ExceptionHandler(RolException.class)
    public ResponseEntity<Map<String, String>> handleRolException(RolException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(403).body(error); // Prohibido
    }

    @ExceptionHandler(OtpException.class)
    public ResponseEntity<Map<String, String>> handleOtpException(OtpException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error); // ✅ Ahora maneja el error de OTP
    }
}
