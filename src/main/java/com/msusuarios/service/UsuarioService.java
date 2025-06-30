package com.msusuarios.service;

import com.msusuarios.dto.*;
import com.msusuarios.entities.Usuario;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest; // importante

import java.util.List;
import java.util.UUID;


import java.util.Map;


public interface UsuarioService {
    String login(LoginRequestDTO loginRequest);
    ResponseEntity<Map<String, Object>> registrarUsuarioFinal(RegisterRequest request);// ✅ Registro final después del OTP
    ResponseEntity<String> createAdmin(UsuarioDTO usuarioDTO);

    ResponseEntity<?> buscarPorEmail(String email);
    Usuario findByUsername(String username);
    List<UsuarioSimpleDTO> listarTodos();
    UsuarioResponseDTO obtenerPorId(UUID id);
    List<UsuarioListadoDTO> listarUsuariosSimple();
    void eliminarUsuarioPorId(UUID userId);
    ResponseEntity<?> actualizarUsuario(UUID id, UsuarioUpdateDTO dto);
    ResponseEntity<?> cambiarPassword(UUID id, CambiarPasswordDTO dto);

    







}
