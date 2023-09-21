package com.example.securitydemo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//Metodos necesarios para generar y validar el token
@Service
public class JwtUtil {
    private String secret = "springboot";

    public String extraerUsuario(String token){
     return extraerClaims(token, Claims::getSubject);
    }

    public Date extraerExpiracion(String token){
        return extraerClaims(token, Claims::getExpiration);
    }

    public <T> T extraerClaims(String token, Function<Claims,T> claimsResolver){//<T> tipo generico
        final Claims claims = extraerTodasClaims(token); //extraer todos los datos del token
        return claimsResolver.apply(claims);
    }

    public Claims extraerTodasClaims(String token){//pasamos el token del cual sacamos la información
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody(); //Firma del Token
        //setSigningKey -> firma el token con la clave secreta
        //parseClaimsJws -> pasa los datos
    }

    private Boolean tokenExpirado(String token){
        return extraerExpiracion(token).before(new Date());
    }

    public String generarToken(String nombreUsuario, String rol){
        Map<String,Object> claims = new HashMap<>();
        claims.put("rol", rol);
        return crearToken(claims,nombreUsuario);
    }

    private String crearToken(Map<String,Object> claims, String subject){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) //entidad a la que pertenece el JWT
                .setIssuedAt(new Date(System.currentTimeMillis())) //fecha en que se utiliza el token
                .setExpiration(new Date(System.currentTimeMillis() + 100 * 60 * 60 * 10))  //fecha expiracion
                .signWith(SignatureAlgorithm.HS256,secret).compact(); //firma
    }

    public Boolean validarToken(String token, UserDetails userDetails){
        final String username = extraerUsuario(token);
        return (username.equals(userDetails.getUsername()) && !tokenExpirado(token)); //si el token no expira y coinciden los datos de usuario es valido
    }
}
