package com.msusuarios.service;

import com.msusuarios.dto.PermissionDTO;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    PermissionDTO create(PermissionDTO dto);
    List<PermissionDTO> getAll();
    PermissionDTO getById(UUID id);
    PermissionDTO update(UUID id, PermissionDTO dto);
    String delete(UUID id);
}
