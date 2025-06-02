package com.msusuarios.service.impl;

import com.msusuarios.dto.RolDTO;
import com.msusuarios.entities.Role;
import com.msusuarios.repository.RoleRepository;
import com.msusuarios.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RoleRepository roleRepository;

    @Override
    public RolDTO crearRol(RolDTO dto) {
        if (roleRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Ya existe un rol con ese nombre.");
        }

        Role nuevo = new Role(UUID.randomUUID(), dto.getName(), dto.getDescription());
        roleRepository.save(nuevo);

        dto.setId(nuevo.getId());
        dto.setCreatedAt(nuevo.getCreatedAt()); // ✅ asignamos fecha de creación al DTO
        return dto;
    }

    @Override
    public List<RolDTO> listarTodos() {
        return roleRepository
                .findAllByOrderByCreatedAtDesc() // 🔥 ordena por fecha desde la DB
                .stream()
                .map(role -> {
                    RolDTO dto = new RolDTO();
                    dto.setId(role.getId());
                    dto.setName(role.getName());
                    dto.setDescription(role.getDescription());
                    dto.setCreatedAt(role.getCreatedAt()); // ✅ importante
                    return dto;
                })
                .collect(Collectors.toList());
    }



    @Override
    public RolDTO obtenerPorId(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        RolDTO dto = new RolDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setCreatedAt(role.getCreatedAt()); // ✅ si se quiere usar por frontend
        return dto;
    }

    @Override
    public RolDTO actualizarRol(UUID id, RolDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Role existente = roleRepository.findByName(dto.getName()).orElse(null);
        if (existente != null && !existente.getId().equals(id)) {
            throw new RuntimeException("Ya existe otro rol con ese nombre");
        }

        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        roleRepository.save(role);

        dto.setId(role.getId());
        dto.setCreatedAt(role.getCreatedAt()); // mantenemos la fecha original
        return dto;
    }

    @Override
    public String eliminarRol(UUID id) {
        if (!roleRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado");
        }

        roleRepository.deleteById(id);
        return "Rol eliminado correctamente";
    }
}
