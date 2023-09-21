package com.example.securitydemo.services.servicesimpl;

import com.example.securitydemo.constantes.ClaseConstantes;
import com.example.securitydemo.pojo.Usuario;
import com.example.securitydemo.repository.UsuarioRepo;
import com.example.securitydemo.security.ClienteDetalleService;
import com.example.securitydemo.security.jwt.JwtUtil;
import com.example.securitydemo.services.UsuarioService;
import com.example.securitydemo.util.ClaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioRepo userRepo;

    @Autowired
    private AuthenticationManager administradorAutentificacion; //Clase con permisos y que indica quien accede a qué

    @Autowired
    private ClienteDetalleService clienteDetalleService;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public ResponseEntity<String> singUp(Map<String, String> requestMap) {
        log.info("Registro interno de Usuario {}", requestMap);
        try {
            if(validarRegistroSingUp(requestMap)){
                Usuario user = userRepo.findByEmail(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userRepo.save(getUserFromMap(requestMap));
                    System.out.println("dentro de save");
                    return ClaseUtils.getResponseEntity("Usuario Creado", HttpStatus.CREATED);
                }
                else {
                    return ClaseUtils.getResponseEntity("El usuario con este email ya existe", HttpStatus.BAD_REQUEST);
                }
            }else {
                return ClaseUtils.getResponseEntity("no entraste aqui", HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ClaseUtils.getResponseEntity(ClaseConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("dentro del login");
        try {
            Authentication autenticacion = administradorAutentificacion.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );
            if(autenticacion.isAuthenticated()){
                if(clienteDetalleService.getDetallesUsuario().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generarToken(
                                    clienteDetalleService.getDetallesUsuario().getEmail(),
                                    clienteDetalleService.getDetallesUsuario().getRol())+"\"}",
                                    HttpStatus.OK);
                }
                else {
                    return new ResponseEntity<String>("{\"mensaje\":\""+"Espere la aprobación del administrador"+"\"}", HttpStatus.BAD_REQUEST);

                }
            }
        }catch (Exception exception){
            log.error("{}",exception);
        }
        return new ResponseEntity<String>("{\"mensaje\":\""+"Credenciales incorrectas"+"\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return userRepo.findAll();
    }

    private boolean validarRegistroSingUp(Map<String,String> requsetMap){
        if (requsetMap.containsKey("nombre") && requsetMap.containsKey("numeroContacto") && requsetMap.containsKey("email") && requsetMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    private Usuario getUserFromMap(Map<String, String> requestMap){
        Usuario user  = new Usuario();
        user.setNombre(requestMap.get("nombre"));
        user.setNumeroContacto(requestMap.get("numeroContacto"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRol("user");
        return user;
    }
}
