package com.example.securitydemo.security.jwt;

import com.example.securitydemo.security.ClienteDetalleService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFiltro extends OncePerRequestFilter { //se extiende de OncePerRequestFilter para que se ejecute sólo una vez

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ClienteDetalleService clienteDetalleService;

    private String nombreUsuario = null;
    Claims claims = null; //Datos: Información del token en el payload

    //filtro interno que valida el usuario
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getServletPath().matches("/api/usuario/login|/api/usuario/recuperarPassword|/api/usuario/registrar")){
            filterChain.doFilter(request,response);
        }else{
            String cabezaAutorizacion = request.getHeader("Authorization");
            String token = null;
            if (cabezaAutorizacion != null && cabezaAutorizacion.startsWith("Bearer ")) { //la cabeza (header) del token comienza siempre con Bearer
                token = cabezaAutorizacion.substring(7); //cortar los primeros 7 caracteres ("Bearer ")
                nombreUsuario = jwtUtil.extraerUsuario(token); //extraer nombreUsuario del token
                claims = jwtUtil.extraerTodasClaims(token); //extraer todas las claims (datos) del token
            }
            if(nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null){ //si no está autenticado
                UserDetails userDetails = clienteDetalleService.loadUserByUsername(nombreUsuario); //cargar el usuario para autenticar
                if(jwtUtil.validarToken(token,userDetails)){ //validarToken
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                    new WebAuthenticationDetailsSource().buildDetails(request);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); //usuario autenticado
                }
            }
            filterChain.doFilter(request,response);
        }
    }

    public Boolean esAdmin(){
        return "admin".equalsIgnoreCase((String) claims.get("rol")); //verificar si es admin
    }

    public Boolean esUsuario(){
        return "usuario".equalsIgnoreCase((String) claims.get("rol")); //verificar si es usuario
    }

    public String getUsuarioActual(){ //verificar usuario Actual
        return nombreUsuario;
    }
}
