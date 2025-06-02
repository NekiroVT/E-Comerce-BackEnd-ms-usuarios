package com.msusuarios.config;

import com.msusuarios.entities.Usuario;
import com.msusuarios.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            throw new UsernameNotFoundException("Usuario sin roles asignados");
        }

        List<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
                .map(usuarioRol -> new SimpleGrantedAuthority("ROLE_" + usuarioRol.getRole().getName()))
                .collect(Collectors.toList());

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                authorities
        );
    }
}
