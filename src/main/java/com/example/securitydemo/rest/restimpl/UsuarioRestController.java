package com.example.securitydemo.rest.restimpl;

import com.example.securitydemo.constantes.ClaseConstantes;
import com.example.securitydemo.pojo.Usuario;
import com.example.securitydemo.services.UsuarioService;
import com.example.securitydemo.util.ClaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioRestController {
    @Autowired
    private UsuarioService usuarioService;
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody(required = true) Map<String,String> requesMap){
        try{
            return usuarioService.singUp(requesMap);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ClaseUtils.getResponseEntity(ClaseConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String,String> requestMap){
        try {
            return usuarioService.login(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return ClaseUtils.getResponseEntity(ClaseConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios(){
        List<Usuario> listaUsuarios = null;
        Map<String, Object> response = new HashMap<>();
        try {
            listaUsuarios = usuarioService.listarUsuarios();
            response.put("size", listaUsuarios.size());
            response.put("rows", listaUsuarios);
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
        }catch (DataAccessException ex){
            response.put("mensaje", "Error al obtener de la base de datos");
            response.put("error", ex.getMessage().concat(": ").concat(ex.getMostSpecificCause().getMessage()));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
