package com.denkitronik.auth.service;


import com.denkitronik.auth.entity.UserInfo;
import com.denkitronik.auth.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * La clase UserInfoService es responsable de cargar la información del usuario desde la base de datos y permitir la
 * adición de nuevos usuarios. También se encarga de la codificación segura de las contraseñas antes de almacenarlas
 * en la base de datos. Esta clase es fundamental para el funcionamiento del sistema de autenticación y autorización
 * implementado con Spring Security.
 */
@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository; // repository to access database

    @Autowired
    private PasswordEncoder encoder; // password encoder


    // This method is used to load user by username

    /***
     * Este método se utiliza para cargar la información del usuario desde la base de datos. El método utiliza el
     * @param username Es el nombre de usuario del usuario que se va a cargar
     * @return Un objeto UserDetails que contiene la información del usuario
     * @throws UsernameNotFoundException Si el usuario no se encuentra en la base de datos
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userDetail = repository.findByName(username);

        // Converting userDetail to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
    }

    // This method is used to add a new user

    /**
     * Este método se utiliza para agregar un nuevo usuario a la base de datos. Antes de agregar el usuario, la
     * @param userInfo Es el objeto UserInfo que se va a agregar a la base de datos
     * @return Un mensaje que indica que el usuario se agregó correctamente
     */
    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "User Added Successfully";
    }
}
