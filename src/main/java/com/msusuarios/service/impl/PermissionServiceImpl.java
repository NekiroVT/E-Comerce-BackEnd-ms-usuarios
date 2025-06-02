package com.msusuarios.service.impl;

import com.msusuarios.dto.PermissionDTO;
import com.msusuarios.entities.Permission;
import com.msusuarios.repository.PermissionRepository;
import com.msusuarios.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public PermissionDTO create(PermissionDTO dto) {
        if (permissionRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Ya existe un permiso con ese nombre");
        }

        Permission permiso = new Permission();
        permiso.setId(UUID.randomUUID());
        permiso.setName(dto.getName());
        permiso.setDescription(dto.getDescription());

        permissionRepository.save(permiso);

        dto.setId(permiso.getId());
        return dto;
    }

    @Override
    public List<PermissionDTO> getAll() {
        return permissionRepository.findAllByOrderByCreatedAtDesc().stream().map(p -> {
            PermissionDTO dto = new PermissionDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setCreatedAt(p.getCreatedAt()); // ✅ Agregado
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public PermissionDTO getById(UUID id) {
        Permission p = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

        PermissionDTO dto = new PermissionDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());

        return dto;
    }

    @Override
    public PermissionDTO update(UUID id, PermissionDTO dto) {
        Permission permiso = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

        // ✅ Validación: no permitir duplicado de nombre con otro permiso distinto
        Permission existente = permissionRepository.findByName(dto.getName()).orElse(null);
        if (existente != null && !existente.getId().equals(id)) {
            throw new RuntimeException("Ya existe otro permiso con ese nombre");
        }

        permiso.setName(dto.getName());
        permiso.setDescription(dto.getDescription());

        permissionRepository.save(permiso);
        dto.setId(permiso.getId());

        return dto;
    }

    @Override
    public String delete(UUID id) {
        if (!permissionRepository.existsById(id)) {
            throw new RuntimeException("Permiso no encontrado");
        }

        permissionRepository.deleteById(id);
        return "Permiso eliminado correctamente";
    }
}
