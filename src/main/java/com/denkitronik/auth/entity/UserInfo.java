package com.denkitronik.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Esta entidad representa la tabla UserInfo en la base de datos, entidad JPA (Java Persistence API) utilizada para
 * representar la información del usuario en la base de datos.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String name;
    private String email;
    private String password;
    private String roles;

}
