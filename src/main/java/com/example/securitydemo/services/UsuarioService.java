package com.example.securitydemo.services;

import com.example.securitydemo.pojo.Usuario;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UsuarioService {
    ResponseEntity<String> singUp(Map<String,String> requestMap);

    ResponseEntity<String> login(Map<String,String> requestMap);

    List<Usuario> listarUsuarios();
}
