package com.example.securitydemo.repository;

import com.example.securitydemo.pojo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepo extends JpaRepository <Usuario,Integer> {
    Usuario findByEmail(@Param("email") String email);

}
