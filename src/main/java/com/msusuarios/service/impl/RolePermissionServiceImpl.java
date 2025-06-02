package com.msusuarios.service.impl;

import com.msusuarios.dto.RolePermisoDTO;
import com.msusuarios.entities.Permission;
import com.msusuarios.entities.Role;
import com.msusuarios.entities.RolePermiso;
import com.msusuarios.entities.RolePermisoId;
import com.msusuarios.repository.PermissionRepository;
import com.msusuarios.repository.RolePermisoRepository;
import com.msusuarios.repository.RoleRepository;
import com.msusuarios.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermisoRepository repo;
    private final RoleRepository roleRepo;
    private final PermissionRepository permRepo;

    @Override
    public void asignar(RolePermisoDTO dto) {
        if (repo.existsByRole_IdAndPermission_Id(dto.getRoleId(), dto.getPermissionId())) {
            throw new RuntimeException("Este permiso ya está asignado a este rol");
        }

        Role role = roleRepo.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Permission permiso = permRepo.findById(dto.getPermissionId())
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

        RolePermiso rp = new RolePermiso(role, permiso);
        repo.save(rp);
    }

    @Override
    public void eliminar(RolePermisoDTO dto) {
        if (!repo.existsByRole_IdAndPermission_Id(dto.getRoleId(), dto.getPermissionId())) {
            throw new RuntimeException("No existe esa relación rol-permiso");
        }

        RolePermisoId id = new RolePermisoId(dto.getRoleId(), dto.getPermissionId());
        repo.deleteById(id);
    }

    @Override
    public List<RolePermisoDTO> listar() {
        return repo.findAll().stream().map(rp -> {
            RolePermisoDTO dto = new RolePermisoDTO();
            dto.setRoleId(rp.getRole().getId());
            dto.setPermissionId(rp.getPermission().getId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RolePermisoDTO> listarPorRolId(UUID roleId) {
        try {
            roleRepo.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol no encontrado");
        }

        return repo.findAll().stream()
                .filter(rp -> rp.getRole().getId().equals(roleId))
                .map(rp -> {
                    RolePermisoDTO dto = new RolePermisoDTO();
                    dto.setRoleId(rp.getRole().getId());
                    dto.setPermissionId(rp.getPermission().getId());
                    return dto;
                }).collect(Collectors.toList());
    }
}
