package com.msusuarios.service.impl;

import com.msusuarios.dto.UsuarioRolDTO;
import com.msusuarios.entities.Role;
import com.msusuarios.entities.Usuario;
import com.msusuarios.entities.UsuarioRol;
import com.msusuarios.entities.UsuarioRolId;
import com.msusuarios.repository.RoleRepository;
import com.msusuarios.repository.UsuarioRepository;
import com.msusuarios.repository.UsuarioRolRepository;
import com.msusuarios.service.UsuarioRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioRolServiceImpl implements UsuarioRolService {

    private final UsuarioRolRepository repo;
    private final UsuarioRepository userRepo;
    private final RoleRepository roleRepo;

    @Override
    public void asignarRolAUsuario(UsuarioRolDTO dto) {
        if (repo.existsByUsuario_IdAndRole_Id(dto.getUserId(), dto.getRoleId())) {
            throw new RuntimeException("Ese usuario ya tiene ese rol");
        }

        Usuario user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        UsuarioRol ur = new UsuarioRol(user, role);
        repo.save(ur);
    }


    @Override
    public void eliminarRolDeUsuario(UsuarioRolDTO dto) {
        // Validar que exista la relación
        if (!repo.existsByUsuario_IdAndRole_Id(dto.getUserId(), dto.getRoleId())) {
            throw new RuntimeException("No existe esa relación usuario-rol");
        }

        // Obtener todos los roles actuales del usuario
        List<UsuarioRol> rolesActuales = repo.findAll().stream()
                .filter(r -> r.getUsuario().getId().equals(dto.getUserId()))
                .collect(Collectors.toList());

        boolean tieneCliente = rolesActuales.stream()
                .anyMatch(r -> r.getRole().getName().equalsIgnoreCase("CLIENTE"));

        boolean estaEliminandoCliente = rolesActuales.stream()
                .anyMatch(r -> r.getRole().getId().equals(dto.getRoleId()) &&
                        r.getRole().getName().equalsIgnoreCase("CLIENTE"));

        // Si el rol a eliminar es CLIENTE y es el único, no se puede eliminar
        if (rolesActuales.size() == 1 && estaEliminandoCliente) {
            throw new RuntimeException("No se puede eliminar el rol CLIENTE si es el único rol del usuario.");
        }

        // Eliminar el rol
        UsuarioRolId id = new UsuarioRolId(dto.getUserId(), dto.getRoleId());
        repo.deleteById(id);

        // Si el usuario se queda sin roles, asignamos CLIENTE automáticamente
        long totalRestante = rolesActuales.stream()
                .filter(r -> !r.getRole().getId().equals(dto.getRoleId()))
                .count();

        if (totalRestante == 0) {
            Role rolCliente = roleRepo.findByName("CLIENTE")
                    .orElseThrow(() -> new RuntimeException("❌ Rol CLIENTE no encontrado"));

            Usuario user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("❌ Usuario no encontrado"));

            repo.save(new UsuarioRol(user, rolCliente));
        }
    }


    @Override
    public List<UsuarioRolDTO> listarTodos() {
        return repo.findAll().stream().map(ur -> {
            UsuarioRolDTO dto = new UsuarioRolDTO();
            dto.setUserId(ur.getUsuario().getId());
            dto.setRoleId(ur.getRole().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UsuarioRolDTO> listarPorUsuarioId(UUID userId) {
        return repo.findAll().stream()
                .filter(ur -> ur.getUsuario().getId().equals(userId))
                .map(ur -> {
                    UsuarioRolDTO dto = new UsuarioRolDTO();
                    dto.setUserId(ur.getUsuario().getId());
                    dto.setRoleId(ur.getRole().getId());
                    return dto;
                }).collect(Collectors.toList());
    }
}