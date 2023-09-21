package com.example.securitydemo.security;

import com.example.securitydemo.pojo.Usuario;
import com.example.securitydemo.repository.UsuarioRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class ClienteDetalleService implements UserDetailsService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    private Usuario detallesUsuario;
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        log.info("Dentro de loadUserByUsername {}", nombreUsuario);
        detallesUsuario = usuarioRepo.findByEmail(nombreUsuario);
        if (!Objects.isNull(detallesUsuario)) {
            return  new User(detallesUsuario.getEmail(), detallesUsuario.getPassword(), new ArrayList<>());
        }else {
            throw  new UsernameNotFoundException("Usuario no encontrado");
        }
    }

    public Usuario getDetallesUsuario() {//metodo get
        return detallesUsuario;
    }
}
