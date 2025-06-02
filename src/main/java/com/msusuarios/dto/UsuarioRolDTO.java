package com.msusuarios.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.msusuarios.util.FlexibleUUIDDeserializer;
import lombok.Data;
import java.util.UUID;

@Data
public class UsuarioRolDTO {

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID userId;

    @JsonDeserialize(using = FlexibleUUIDDeserializer.class)
    private UUID roleId;
}
