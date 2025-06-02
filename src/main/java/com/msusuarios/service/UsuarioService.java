package com.msusuarios.service;

import com.msusuarios.dto.LoginRequestDTO;
import com.msusuarios.dto.RegisterRequest;
import com.msusuarios.dto.UsuarioDTO;
import com.msusuarios.entities.Usuario;
import org.springframework.http.ResponseEntity;
import com.msusuarios.dto.UsuarioResponseDTO;
import com.msusuarios.dto.UsuarioSimpleDTO;
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



}
