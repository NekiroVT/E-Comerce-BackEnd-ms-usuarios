package com.msusuarios.controller;

import com.msusuarios.config.JwtTokenProvider;
import com.msusuarios.dto.*;
import com.msusuarios.entities.Usuario;
import com.msusuarios.repository.UsuarioRepository;
import com.msusuarios.service.UsuarioService;
import com.msusuarios.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        // 🧠 Buscar usuario por username
        Usuario usuario = usuarioService.findByUsername(loginRequest.getUsername());

        // 🔐 Validar contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("success", false, "message", "❌ Contraseña incorrecta")
            );
        }

        // 🔑 Generar tokens
        String accessToken = jwtTokenProvider.generateToken(usuario);
        String refreshToken = jwtTokenProvider.generateRefreshToken(usuario);

        // 💾 Guardar refresh token en BD
        usuario.setRefreshToken(refreshToken);
        usuarioRepository.save(usuario);

        // 🎯 Obtener permisos desde los roles
        List<String> permisos = usuario.getRoles().stream()
                .flatMap(r -> r.getRole().getPermisos().stream())
                .map(p -> p.getPermission().getName())
                .distinct()
                .collect(Collectors.toList());

        // 📦 Retornar respuesta
        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", accessToken,
                "refreshToken", refreshToken,
                "username", usuario.getUsername(),
                "permisos", permisos
        ));
    }


    // ♻ REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");

        System.out.println("🔎 TOKEN RECIBIDO ===> " + refreshToken);

        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "❌ refreshToken no enviado")
            );
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("success", false, "message", "❌ Refresh token inválido o expirado")
            );
        }

        String userIdStr = jwtTokenProvider.getUserIdFromToken(refreshToken);
        UUID userId = UUID.fromString(userIdStr);

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("❌ Usuario no encontrado"));

        if (!refreshToken.equals(usuario.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("success", false, "message", "❌ Refresh token no coincide con el registrado")
            );
        }

        // ✅ Solo generamos un nuevo accessToken
        String nuevoAccessToken = jwtTokenProvider.generateToken(usuario);

        // ❌ No se rota el refreshToken
        return ResponseEntity.ok(Map.of(
                "success", true,
                "accessToken", nuevoAccessToken,
                "refreshToken", refreshToken // Se reusa el mismo
        ));
    }



    // ✅ GENERAR OTP
    @PostMapping("/generate-otp")
    public ResponseEntity<Map<String, Object>> generateOtp(@RequestBody GenerateOtpRequest request) {
        String email = request.getEmail();

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(email);

        if (usuarioOpt.isPresent()) {
            Usuario existente = usuarioOpt.get();
            if ("active".equalsIgnoreCase(existente.getStatus())) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "❌ El correo ya está registrado. Inicia sesión."
                ));
            }
        }

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
        return usuarioService.registrarUsuarioFinal(registerRequest);
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
