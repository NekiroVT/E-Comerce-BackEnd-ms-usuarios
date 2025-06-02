package com.msusuarios.config;

import com.msusuarios.entities.*;
import com.msusuarios.repository.PermissionRepository;
import com.msusuarios.repository.RolePermisoRepository;
import com.msusuarios.repository.RoleRepository;
import com.msusuarios.repository.UsuarioRepository;
import com.msusuarios.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class RoleAndUserInitializer {

    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolePermisoRepository rolePermisoRepository;

    @Bean
    CommandLineRunner initRolesAndUsers(PermissionRepository permissionRepository) {
        return args -> {
            // 1. Crear permisos si no existen
            Permission permisoCrearPermisos = permissionRepository.findByName("usuarios:permisos.create")
                    .orElseGet(() -> permissionRepository.save(
                            new Permission(UUID.randomUUID(), "usuarios:permisos.create", "Permiso para crear permisos")));

            Permission permisoRolesPermisos = permissionRepository.findByName("usuarios:rolespermisos.create")
                    .orElseGet(() -> permissionRepository.save(
                            new Permission(UUID.randomUUID(), "usuarios:rolespermisos.create", "Permiso para crear roles y permisos")));

            // 2. Crear rol ADMIN si no existe
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(
                            new Role(UUID.randomUUID(), "ADMIN", "Rol administrador")));

            // 3. Asignar permisos al rol ADMIN si no los tiene
            if (!rolePermisoRepository.existsByRole_IdAndPermission_Id(adminRole.getId(), permisoCrearPermisos.getId())) {
                rolePermisoRepository.save(new RolePermiso(adminRole, permisoCrearPermisos));
                System.out.println("🔗 Permiso 'usuarios:permisos.create' asignado al rol ADMIN");
            }

            if (!rolePermisoRepository.existsByRole_IdAndPermission_Id(adminRole.getId(), permisoRolesPermisos.getId())) {
                rolePermisoRepository.save(new RolePermiso(adminRole, permisoRolesPermisos));
                System.out.println("🔗 Permiso 'usuarios:rolespermisos.create' asignado al rol ADMIN");
            }

            // 4. Crear rol CLIENTE si no existe
            roleRepository.findByName("CLIENTE")
                    .orElseGet(() -> roleRepository.save(
                            new Role(UUID.randomUUID(), "CLIENTE", "Rol cliente por defecto")));

            // 5. Crear usuario admin si no existe
            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario adminUser = new Usuario();
                adminUser.setId(UUID.randomUUID());
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@admin.com");
                adminUser.setPassword(new BCryptPasswordEncoder().encode("admin123"));
                adminUser.setFirstName("Admin");
                adminUser.setLastName("Master");
                adminUser.setBirthdate(LocalDate.of(1990, 1, 1));
                adminUser.setIsVerified(true);
                adminUser.setStatus("active");
                adminUser.setProfilePhotoUrl("/uploads/defaults/default_admin.png");

                usuarioRepository.save(adminUser);

                UsuarioRol relacion = new UsuarioRol(adminUser, adminRole);
                usuarioRolRepository.save(relacion);

                System.out.println("✅ Admin creado correctamente con rol y permisos asignados");
            }
        };
    }
}
