package com.denkitronik.auth.controller;

import com.denkitronik.auth.entity.AuthRequest;
import com.denkitronik.auth.entity.UserInfo;
import com.denkitronik.auth.service.JwtService;
import com.denkitronik.auth.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

/**
 * Esta clase controla las acciones relacionadas con la autenticación de usuarios, la creación de perfiles y la
 * generación de tokens JWT para la autenticación en la aplicación. Los métodos están protegidos por anotaciones de
 * autorización para garantizar que solo los usuarios con los roles adecuados puedan acceder a ciertas rutas.
 */
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserInfoService service; // inyecta una instancia del servicio UserInfoService, que se utiliza para gestionar la información del usuario en la base de datos.

    @Autowired
    private JwtService jwtService; // Inyecta una instancia del servicio JwtService, que se utiliza para generar tokens JWT y gestionar la autenticación.

    @Autowired
    private AuthenticationManager authenticationManager; // Inyecta el AuthenticationManager, que se utiliza para autenticar a los usuarios.

    /**
     * Este método maneja las solicitudes GET a "/auth/welcome" y simplemente devuelve un mensaje de bienvenida.
     * @return Un mensaje de bienvenida
     */
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    /**
     * Este método maneja las solicitudes POST a "/auth/addNewUser" y crea un nuevo usuario en la base de datos.
     * @param userInfo Es el objeto UserInfo que se va a guardar en la base de datos
     * @return Un mensaje de confirmación de que el usuario se ha creado correctamente
     */
    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        return service.addUser(userInfo);
    }

    /**
     * Este método maneja las solicitudes GET a "/auth/userProfile" y devuelve un mensaje de bienvenida si el usuario
     * está autenticado. De lo contrario, devuelve un mensaje de error.
     * Este método está protegido por la anotación @PreAuthorize, que garantiza que solo los usuarios con el rol
     * "ROLE_USER" puedan acceder a este método.
     * @return Un mensaje de bienvenida si el usuario está autenticado
     */
    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile() {
        return "Welcome to User Profile";
    }

    /**
     * Este método maneja las solicitudes GET a "/auth/adminProfile" y devuelve un mensaje de bienvenida si el usuario
     * está autenticado. De lo contrario, devuelve un mensaje de error.
     * Este método está protegido por la anotación @PreAuthorize, que garantiza que solo los usuarios con el rol
     * "ROLE_ADMIN" puedan acceder a este método.
     * @return Un mensaje de bienvenida si el usuario está autenticado
     */
    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

    /**
     * Este método maneja las solicitudes POST a "/auth/generateToken". Recibe un objeto AuthRequest en el cuerpo de la
     * solicitud (mediante la anotación @RequestBody). Luego, utiliza el AuthenticationManager para autenticar al
     * usuario mediante un nombre de usuario y contraseña. Si la autenticación es exitosa, se llama al servicio
     * JwtService para generar un token JWT y se devuelve ese token como respuesta. Si la autenticación falla, se lanza
     * una excepción UsernameNotFoundException.
     * @param authRequest Es el objeto AuthRequest que contiene el nombre de usuario y la contraseña del usuario
     * @return El token JWT generado
     */
    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

}
