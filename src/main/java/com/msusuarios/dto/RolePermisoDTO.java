package com.msusuarios.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msusuarios.util.FlexibleUUIDDeserializer;
import lombok.Data;

import java.util.UUID;

@Data
public class RolePermisoDTO {

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID roleId;

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID permissionId;
}
