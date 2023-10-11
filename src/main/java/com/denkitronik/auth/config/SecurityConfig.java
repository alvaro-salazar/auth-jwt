package com.denkitronik.auth.config;


import com.denkitronik.auth.filter.JwtAuthFilter;
import com.denkitronik.auth.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


// Imagina que estás construyendo una casa y estás configurando la seguridad de la casa.
/**
 * Esta clase se utiliza para configurar la seguridad de la aplicación. Aquí es donde se establecen las reglas de seguridad para la aplicación.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Tienes una cerradura especial que protege la puerta de tu casa, y esta cerradura se llama JwtAuthFilter.
    @Autowired
    private JwtAuthFilter authFilter; // jwt auth filter to validate the token and extract the username from the token


    // Has contratado a alguien (llamado UserInfoService) para llevar un registro de todas las personas que tienen
    // permiso para entrar a tu casa. Esta persona sabe quiénes son los dueños de las llaves y quiénes no.

    /**
     * Este método se utiliza para crear un objeto UserDetailsService que se utiliza para crear un usuario y guardarlo
     * @return UserDetailsService objeto que se utiliza para crear un usuario y guardarlo en la base de datos
     */
    @Bean // This method is used to create a user and save it in the database
    public UserDetailsService userDetailsService() {
        return new UserInfoService();
    }


    // Estás configurando las reglas para tu casa. Dices quién puede entrar libremente, quién necesita una llave
    // especial y cómo se debe comportar la seguridad en general. Por ejemplo, podrías decir que todos pueden entrar
    // a la sala de estar, pero solo las personas con una llave especial pueden entrar a la habitación principal.

    /**
     * Este método se utiliza para configurar la seguridad de la aplicación. Aquí es donde se establecen las reglas
     * @param http Es el objeto HttpSecurity que se utiliza para configurar la seguridad de la aplicación
     * @return SecurityFilterChain el objeto que se utiliza para configurar la seguridad HttpSecurity de la aplicación
     * @throws Exception Excepción que se lanza si hay un error al configurar la seguridad de la aplicación
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.csrf(AbstractHttpConfigurer::disable)                 // (1) Deshabilitar la prevención de ataques CSRF.
                .authorizeHttpRequests((authorized)->authorized     // (2) Establecer quién tiene permiso para entrar.
                .requestMatchers("/auth/welcome", "/auth/addNewUser", "/auth/generateToken").permitAll() // (3) Permitir a cualquiera entrar a ciertos lugares sin verificar su identidad.
                .requestMatchers("/auth/user/**").authenticated() // (4) Requerir autenticación para acceder a ciertas áreas.
                .requestMatchers("/auth/admin/**").authenticated()); // (5) Requerir autenticación adicional para áreas muy seguras.
         http.sessionManagement((sessions)-> sessions
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // (6) Configurar la administración de sesiones como "sin estado".
         http.authenticationProvider(authenticationProvider()) // (7) Establecer quién verificará las credenciales de las personas.
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class); // (8) Agregar un filtro de autenticación antes de que las personas entren.
         return http.build(); // (9) Finalizar y construir las reglas de seguridad.
    }

    // (1) Analogía: Imagina que tu casa tiene un sistema de seguridad que a veces verifica si las personas
    // que ingresan tienen pulseras de seguridad especiales. Aquí, estás desactivando ese sistema para algunas áreas.
    // CSRF es un tipo de ataque que puede ocurrir cuando un sitio web malicioso envía solicitudes a un sitio web legítimo.
    // (2) Analogía: Estás colocando un guardia en la puerta de tu casa y dándole una lista de personas que pueden
    // entrar y las que no pueden.
    // (3) Analogía: Estás diciendo que en ciertas áreas de tu casa (por ejemplo, la sala de estar), cualquiera puede
    // entrar sin ser revisado.
    // (4) Analogía: Para algunas habitaciones importantes de tu casa (como tu habitación principal), solo las personas
    // con la identificación adecuada (por ejemplo, una llave especial) pueden entrar.
    // (5) Analogía: Imagina que tienes una bóveda secreta en tu casa. Solo las personas con una identificación
    // superespecial pueden ingresar.
    // (6) Analogía: Estás diciendo que no quieres mantener un registro de quién ha estado en tu casa.
    // Una vez que alguien sale, olvidas quiénes son.
    // (7) Analogía: Has contratado a un experto en seguridad (anteriormente mencionado) para verificar las
    // credenciales de las personas. Este experto utiliza la lista de personas con llaves y la máquina mágica de
    // contraseñas.
    // (8) Analogía: Has instalado una puerta especial antes de la puerta principal de tu casa que verifica las
    // credenciales de las personas antes de dejarlas entrar.
    // (9) Analogía: Has completado el diseño de tu entrada y las reglas de seguridad. Tu casa ahora está lista para
    // protegerse de manera efectiva.




    // Tienes una máquina mágica que convierte las contraseñas en algo que nadie más puede entender.
    // Es como si tuvieras una máquina que convierte "contraseña" en "gobbledygook" y solo tú puedes traducirlo
    // de vuelta a "contraseña".
    /**
     * Este método se utiliza para crear un objeto PasswordEncoder que se utiliza para codificar la contraseña del usuario
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // Tienes un experto en seguridad que decide cómo comprobar si alguien debe entrar a tu casa.
    // Este experto usa la lista de personas con llaves y la máquina mágica para verificar si
    // las credenciales son correctas.

    /**
     * Este método se utiliza para crear un objeto AuthenticationProvider que se utiliza para autenticar al usuario
     * @return AuthenticationProvider es el objeto que se utiliza para autenticar al usuario usando DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    // Has contratado a un administrador de seguridad que es responsable de verificar las credenciales
    // de las personas que intentan entrar a tu casa. Este administrador sabe cómo usar las reglas de seguridad
    // y el experto en seguridad para tomar decisiones sobre quién puede entrar y quién no.

    /**
     * Este metodo se utiliza para crear un objeto bean AuthenticationManager que se utiliza para autenticar al usuario
     * @param config Es la configuración de autenticación que se utiliza para crear un AuthenticationManager bean
     * @return AuthenticationManager es el objeto que se utiliza para autenticar al usuario usando la configuración de autenticación
     * @throws Exception Excepción que se lanza si hay un error al crear el AuthenticationManager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
