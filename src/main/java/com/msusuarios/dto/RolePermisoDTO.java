package com.msusuarios.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msusuarios.util.FlexibleUUIDDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RolePermisoDTO {
    private UUID roleId;
    private UUID permissionId;
    private String roleName;
    private String permissionName;
    private LocalDateTime createdAt;
}

