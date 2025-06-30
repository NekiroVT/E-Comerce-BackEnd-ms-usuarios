package com.msusuarios.service.impl;

import com.msusuarios.config.JwtTokenProvider;
import com.msusuarios.dto.*;
import com.msusuarios.entities.*;
import com.msusuarios.exception.LoginException;
import com.msusuarios.repository.*;
import com.msusuarios.service.EmailService;
import com.msusuarios.service.FirebaseStorageService;
import com.msusuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final EmailService emailService;
    private final FirebaseStorageService firebaseStorageService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new LoginException("❌ Usuario no encontrado."));
    }

    @Override
    public String login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new LoginException("❌ Usuario no encontrado."));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new LoginException("❌ Contraseña incorrecta.");
        }

        if (!usuario.getIsVerified()) {
            throw new LoginException("❌ Debes verificar tu correo primero.");
        }

        if (!"active".equalsIgnoreCase(usuario.getStatus())) {
            throw new LoginException("❌ Tu cuenta no está activa.");
        }

        return jwtTokenProvider.generateToken(usuario);
    }

    @Override
    @Transactional
    public ResponseEntity<Map<String, Object>> registrarUsuarioFinal(RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();

        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim();

        if (usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
            response.put("success", false);
            response.put("message", "❌ Este correo ya está registrado.");
            return ResponseEntity.badRequest().body(response);
        }

        if (usuarioRepository.findByUsername(username).isPresent()) {
            response.put("success", false);
            response.put("message", "❌ Este nombre de usuario ya existe.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail(request.getEmail());
        usuario.setUsername(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setFirstName(request.getFirstName());
        usuario.setLastName(request.getLastName());
        usuario.setBirthdate(request.getBirthdate());
        usuario.setProfilePhotoUrl(request.getProfilePhotoUrl());
        usuario.setStatus("active");
        usuario.setIsVerified(true);

        usuarioRepository.save(usuario);

        Role rolCliente = roleRepository.findByName("CLIENTE")
                .orElseThrow(() -> new RuntimeException("❌ Rol CLIENTE no encontrado."));

        UsuarioRol usuarioRol = new UsuarioRol(usuario, rolCliente);
        usuarioRolRepository.save(usuarioRol);

        response.put("success", true);
        response.put("message", "✅ Cuenta registrada correctamente.");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> buscarPorEmail(String email) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailIgnoreCase(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("email", usuario.getEmail());
        response.put("username", usuario.getUsername());
        response.put("firstName", usuario.getFirstName());
        response.put("lastName", usuario.getLastName());
        response.put("birthdate", usuario.getBirthdate());
        response.put("profilePhotoUrl", usuario.getProfilePhotoUrl());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> createAdmin(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(
                            new Role(UUID.randomUUID(), "ADMIN", "Rol de administrador")));

            Usuario usuario = new Usuario();
            usuario.setId(UUID.randomUUID());
            usuario.setUsername(usuarioDTO.getUsername());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
            usuario.setIsVerified(true);
            usuario.setStatus("active");

            usuarioRepository.save(usuario);
            usuarioRolRepository.save(new UsuarioRol(usuario, adminRole));

            return ResponseEntity.ok("✅ Admin creado exitosamente");
        } else {
            return ResponseEntity.status(400).body("❌ Ya existe un admin");
        }
    }

    // 🔥 NUEVOS MÉTODOS

    @Override
    public List<UsuarioSimpleDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> new UsuarioSimpleDTO(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getEmail(),
                        usuario.getFirstName(),
                        usuario.getLastName(),
                        usuario.getBirthdate(),
                        usuario.getProfilePhotoUrl(),
                        usuario.getIsVerified(),
                        usuario.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDTO obtenerPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<String> roles = usuario.getRoles().stream()
                .map(usuarioRol -> usuarioRol.getRole().getName()) // 👈 este es correcto
                .collect(Collectors.toList());


        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .firstName(usuario.getFirstName())
                .lastName(usuario.getLastName())
                .birthdate(usuario.getBirthdate())
                .profilePhotoUrl(usuario.getProfilePhotoUrl())
                .isVerified(usuario.getIsVerified())
                .status(usuario.getStatus())
                .roles(roles)
                .build();
    }
    @Override
    public List<UsuarioListadoDTO> listarUsuariosSimple() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> new UsuarioListadoDTO(
                        usuario.getId(),
                        usuario.getUsername(),
                        usuario.getEmail(),
                        usuario.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarUsuarioPorId(UUID userId) {
        usuarioRolRepository.deleteByUsuario_Id(userId); // ✅ elimina relaciones primero
        usuarioRepository.deleteById(userId);            // ✅ luego el usuario
    }

    @Override
    @Transactional
    public ResponseEntity<?> actualizarUsuario(UUID id, UsuarioUpdateDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "❌ Usuario no encontrado");
            return ResponseEntity.status(404).body(response);
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setUsername(dto.getUsername());
        usuario.setEmail(dto.getEmail());
        usuario.setFirstName(dto.getFirstName());
        usuario.setLastName(dto.getLastName());
        usuario.setBirthdate(dto.getBirthdate());
        usuario.setProfilePhotoUrl(dto.getProfilePhotoUrl());
        usuario.setStatus(dto.getStatus());

        usuarioRepository.save(usuario);

        response.put("success", true);
        response.put("message", "✅ Usuario actualizado");
        return ResponseEntity.ok(response);
    }


    @Override
    @Transactional
    public ResponseEntity<?> cambiarPassword(UUID id, CambiarPasswordDTO dto) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "❌ Usuario no encontrado");
            return ResponseEntity.status(404).body(response);
        }

        Usuario usuario = usuarioOpt.get();
        String nuevaPassword = passwordEncoder.encode(dto.getNuevaPassword());
        usuario.setPassword(nuevaPassword);

        usuarioRepository.save(usuario);

        response.put("success", true);
        response.put("message", "✅ Contraseña actualizada correctamente");
        return ResponseEntity.ok(response);
    }




    











}
