package com.denkitronik.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Objeto de Transferencia de Datos) utilizado para representar las solicitudes de autenticación que llegan a
 * tu aplicación
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    private String username;
    private String password;

}
