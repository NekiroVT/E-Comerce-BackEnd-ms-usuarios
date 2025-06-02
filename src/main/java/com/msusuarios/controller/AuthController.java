package com.msusuarios.controller;

import com.msusuarios.config.JwtTokenProvider;
import com.msusuarios.dto.*;
import com.msusuarios.entities.Usuario;
import com.msusuarios.repository.UsuarioRepository;
import com.msusuarios.service.UsuarioService;
import com.msusuarios.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;
    private final UsuarioRepository usuarioRepository;

    // ✅ LOGIN
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Usuario usuario = usuarioService.findByUsername(loginRequest.getUsername());
        String token = usuarioService.login(loginRequest);

        // extraer permisos desde sus roles
        List<String> permisos = usuario.getRoles().stream()
                .flatMap(r -> r.getRole().getPermisos().stream())
                .map(p -> p.getPermission().getName())
                .distinct()
                .toList();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "permisos", permisos
        ));
    }



    // ✅ GENERAR OTP
    @PostMapping("/generate-otp")
    public ResponseEntity<Map<String, Object>> generateOtp(@RequestBody GenerateOtpRequest request) {
        String email = request.getEmail();

        // 📌 Verifica si ya existe un usuario con ese email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(email);

        if (usuarioOpt.isPresent()) {
            Usuario existente = usuarioOpt.get();

            // 🚫 Si ya está activo, no se genera OTP
            if ("active".equalsIgnoreCase(existente.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "❌ El correo ya está registrado. Inicia sesión."
                ));
            }
        }

        // ✅ Si no está registrado o está inactivo, se envía el OTP
        otpService.enviarOtp(email);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "✅ OTP enviado exitosamente."
        ));
    }


    // ✅ VERIFICAR OTP
    @PostMapping("/verificar-otp")
    public ResponseEntity<Map<String, Object>> verificarOtp(@RequestBody VerificarOtpRequest request) {
        otpService.verificarOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "✅ Código verificado correctamente."
        ));
    }

    // ✅ REGISTRAR USUARIO FINAL
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest registerRequest) {
        return usuarioService.registrarUsuarioFinal(registerRequest); // ✅ usar el response real
    }


    // ✅ BUSCAR DATOS TEMPORALES POR EMAIL
    @GetMapping("/buscar-por-email")
    public ResponseEntity<Map<String, Object>> buscarPorEmail(@RequestParam String email) {
        Object usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", usuario
        ));
    }

    // ✅ CREAR ADMIN
    @PostMapping("/create-admin")
    public ResponseEntity<Map<String, Object>> createAdmin(@RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.createAdmin(usuarioDTO);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "✅ Administrador creado correctamente."
        ));
    }

    // ✅ TIEMPO RESTANTE DEL OTP
    @GetMapping("/otp-tiempo-restante")
    public ResponseEntity<Map<String, Object>> tiempoRestanteOtp(@RequestParam String email) {
        long tiempoRestante = otpService.obtenerTiempoRestante(email);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "tiempoRestante", tiempoRestante
        ));
    }

    // ✅ INFO DEL USUARIO LOGUEADO
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioLogueado(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().build();
            }

            String token = authHeader.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(401).build();
            }

            String username = jwtTokenProvider.getUsernameFromToken(token);
            Usuario usuario = usuarioService.findByUsername(username);

            UsuarioResponseDTO response = UsuarioResponseDTO.builder()
                    .id(usuario.getId())
                    .username(usuario.getUsername())
                    .email(usuario.getEmail())
                    .firstName(usuario.getFirstName())
                    .lastName(usuario.getLastName())
                    .birthdate(usuario.getBirthdate())
                    .profilePhotoUrl(usuario.getProfilePhotoUrl())
                    .isVerified(usuario.getIsVerified())
                    .status(usuario.getStatus())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    // ✅ REENVIAR OTP
    @PostMapping("/reenviar-otp")
    public ResponseEntity<Map<String, Object>> reenviarOtp(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "❌ El email es requerido."
            ));
        }

        otpService.reenviarOtp(email);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "✅ Nuevo OTP enviado correctamente."
        ));
    }
}
