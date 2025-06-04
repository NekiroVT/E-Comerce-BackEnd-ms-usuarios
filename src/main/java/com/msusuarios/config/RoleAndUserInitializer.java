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
            String[][] permisos = {
                    {"usuarios:usuarios.update", "Permiso para actualizar usuarios"},
                    {"usuarios:usuarios.delete", "Permiso para eliminar usuarios"},
                    {"usuarios:rolespermisos.get.id", "Permiso para ver permisos de un rol por ID"},
                    {"usuarios:rolespermisos.get", "Permiso para listar permisos de roles"},
                    {"usuarios:rolespermisos.delete", "Permiso para eliminar relación rol-permiso"},
                    {"panel-admin:roleperm", "Permiso para administrar rol-permiso desde el panel admin"},
                    {"usuarios:userroles.delete", "Permiso para eliminar un rol de un usuario"},
                    {"usuarios:userroles.get", "Permiso para listar los usuarios con sus roles"},
                    {"panel-admin:userrol", "Permiso para administrar roles de usuarios desde el panel admin"},
                    {"usuarios:userroles.create", "Permiso para asignar un rol a un usuario"},
                    {"usuarios:roles.delete", "Permiso para eliminar roles"},
                    {"usuarios:roles.update", "Permiso para actualizar roles"},
                    {"panel-admin:roles", "Permiso para administrar roles desde el panel admin"},
                    {"ver:admin", "Permiso para ver opciones administrativas"},
                    {"usuarios:rolespermisos.create", "Permiso para crear relaciones entre roles y permisos"},
                    {"ver:admin.permisos", "Permiso para ver opción de administración de permisos"},
                    {"usuarios:permisos.update", "Permiso para editar permisos"},
                    {"panel-admin:permisos", "Permiso para ver los permisos desde el panel"},
                    {"usuarios:permisos.delete", "Permiso para eliminar permisos"},
                    {"panel-admin:usuarios", "Permiso para ver usuarios desde el panel admin"},
                    {"usuarios:usuarios.get", "Permiso para listar todos los usuarios"},
                    {"usuarios:usuarios.get.id", "Permiso para ver detalles de un usuario"},
                    {"usuarios:roles.create", "Permiso para crear roles"},
                    {"panel-admin:home", "Permiso para ver home"},
                    {"panel:comprobantes", "Permiso para ver opción del panel admin comprobantes"},
                    {"ver:panel.admin", "Permiso para ver el panel de admin"},
                    {"categorias:categoria.get.id", "Permiso para ver categorías por ID"},
                    {"categorias:categoria.get", "Permiso para ver todas las categorías"},
                    {"categorias:categoria.create", "Permiso para crear categorías"},
                    {"productos:productos.create", "Permiso para crear productos"},
                    {"ver:admin.productos", "Permiso para ver opción de admin.productos"},
                    {"usuarios:permisos.get", "Permiso para listar permisos"},
                    {"usuarios:roles.get", "Permiso para listar roles"},
                    {"usuarios:usuarios.update.bypassword", "Permiso admin para cambiar las contraseñas"},
                    {"usuarios:permisos.create", "Permiso para crear permisos"}
            };

            for (String[] p : permisos) {
                permissionRepository.findByName(p[0])
                        .orElseGet(() -> permissionRepository.save(
                                new Permission(UUID.randomUUID(), p[0], p[1])));
            }

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(
                            new Role(UUID.randomUUID(), "ADMIN", "Rol administrador")));

            for (String[] p : permisos) {
                Permission permiso = permissionRepository.findByName(p[0]).get();
                if (!rolePermisoRepository.existsByRole_IdAndPermission_Id(adminRole.getId(), permiso.getId())) {
                    rolePermisoRepository.save(new RolePermiso(adminRole, permiso));
                }
            }

            roleRepository.findByName("CLIENTE")
                    .orElseGet(() -> roleRepository.save(
                            new Role(UUID.randomUUID(), "CLIENTE", "Rol cliente por defecto")));

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
                usuarioRolRepository.save(new UsuarioRol(adminUser, adminRole));

                System.out.println("✅ Admin creado correctamente con rol y permisos asignados");
            }
        };
    }
}