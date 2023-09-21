package com.example.securitydemo.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "User.findByEmail", query = "select u from Usuario u where u.email=:email")

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String nombre;
    @Column
    private String numeroContacto;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private String status;
    @Column
    private String rol;
}
