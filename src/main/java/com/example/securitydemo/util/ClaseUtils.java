package com.example.securitydemo.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ClaseUtils {
    private ClaseUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String message, HttpStatus http){
        return new ResponseEntity<String>("Mensaje: "+ message, http);
    }
}
