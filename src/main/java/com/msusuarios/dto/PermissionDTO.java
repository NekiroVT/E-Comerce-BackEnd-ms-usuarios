package com.msusuarios.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PermissionDTO {
    private UUID id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

}
