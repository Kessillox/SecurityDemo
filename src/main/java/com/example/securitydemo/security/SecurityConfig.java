package com.example.securitydemo.security;

import com.example.securitydemo.security.jwt.JwtFiltro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.HttpSecurityDsl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ClienteDetalleService clienteDetalleService;

    @Autowired
    private JwtFiltro jwtFiltro;

    @Bean
    public PasswordEncoder codificarPassword(){ //codificar contraseña
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    protected SecurityFilterChain cadenaFiltroSeguridad(HttpSecurity httpSecurity) throws Exception {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT","OPTIONS","PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));


        httpSecurity /*.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()*/
                //.exceptionHandling(exception-> exception)
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) //Cross Site Request Forgery (csrf) vulnerabilidad tecnica de falsicicacion de petición en sitios cruzados
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize)-> authorize
                .requestMatchers(
                        "/api/usuario/login","/api/usuario/registrar", "/api/usuario/recuperarPassword")
                .permitAll()
                .anyRequest()
                .authenticated());
        httpSecurity.addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager administradorAutentificacion(AuthenticationConfiguration configuracionAutentificacion) throws Exception{
        return configuracionAutentificacion.getAuthenticationManager();
    }

}
