package com.denkitronik.auth.filter;


import com.denkitronik.auth.service.JwtService;
import com.denkitronik.auth.service.UserInfoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Esta clase nos ayuda a validar el token JWT generado y extraer el nombre de usuario del token. Además, establece
// la autenticación en el "security context holder" si el token es válido y el usuario se autentica correctamente.
// Luego, la solicitud se reenvía al siguiente filtro en la cadena de filtros. Sin embargo, si el token no es válido
// o el usuario no se auténtica correctamente, la solicitud se rechaza y se envía una respuesta al cliente con el
// mensaje de error "acceso no autorizado" y el código de estado 401 (acceso no autorizado). En este caso, la solicitud
// no se reenvía al siguiente filtro en la cadena.


/**
 * Actúa como un filtro para procesar las solicitudes en una aplicación web. Esta clase se asegura de que el filtro
 * se aplique una vez por cada solicitud.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // Servicio de token jwt para validar el token y extraer el nombre de usuario del token

    @Autowired
    private UserInfoService userDetailsService; // Servicio de detalles de usuario para cargar los detalles del usuario

    /**
     * Este método se utiliza para validar el token jwt generado y extraer el nombre de usuario del token y establecer
     * Este es un método especial que se llama cada vez que se recibe una solicitud. Aquí es donde ocurre
     * la acción principal del filtro.
     * @param request La solicitud que se está procesando
     * @param response La respuesta que se enviará al cliente
     * @param filterChain El filtro de cadena es una interfaz que proporciona un mecanismo para invocar todos los filtros
     * @throws ServletException Esta excepción se lanza cuando se produce un error en el servlet
     * @throws IOException Esta excepción se lanza cuando se produce un error de entrada/salida
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //En esta parte, estamos revisando si la solicitud incluye un encabezado de "Authorization" y si comienza con
        // la palabra "Bearer". Esto es típico en las solicitudes de autenticación basadas en tokens JWT.
        // Si es así, extraemos el token JWT y el nombre de usuario del token.
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        // Aquí estamos verificando si tenemos un nombre de usuario y si no hay autenticación en curso en el sistema.
        // Si tenemos un nombre de usuario, cargamos los detalles del usuario y validamos el token JWT.
        // Si el token es válido, creamos una autenticación exitosa y la establecemos en el sistema para que la
        // aplicación sepa que el usuario está autenticado.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // Esto verifica si el usuario no está autenticado actualmente en el sistema
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Esto carga los detalles del usuario
            if (jwtService.validateToken(token, userDetails)) {  // Esto valida el token JWT
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Esto agrega información adicional sobre la solicitud de autenticación, como la dirección IP del cliente, al objeto authToken que representa la autenticación exitosa del usuario
                SecurityContextHolder.getContext().setAuthentication(authToken);  // Esto establece la autenticación en el contexto de seguridad de Spring
            }
        }

        // Finalmente, después de este proceso, permitimos que la solicitud continúe su flujo normal.
        // Esto significa que la solicitud se dirigirá a la parte de la aplicación que corresponda a la ruta solicitada.
        filterChain.doFilter(request, response);
    }
}

/*
Primero, el filtro comprueba si tienes una invitación (token JWT) en tu bolsillo (la solicitud que haces).
Busca en tu bolsillo y encuentra una tarjeta que dice "Authorization". Si no hay nada en tu bolsillo o si la tarjeta
no dice "Bearer", no puedes entrar.
Si tienes la tarjeta de "Authorization" y comienza con "Bearer", saca la invitación real (el token JWT) de la tarjeta.
Luego, mira la invitación y verifica quién está invitado (el nombre de usuario en el token).
Si tu nombre está en la invitación y aún no has entrado a la fiesta (nadie más ha sido autenticado), entonces puedes
entrar. La persona en la entrada verifica si tu invitación es auténtica (el filtro verifica si el token JWT es válido).
Si es auténtica, pasas y te dejan entrar a la fiesta.
Después de esto, independientemente de si pudiste entrar o no, se permite que otros sigan entrando a la fiesta
(la solicitud continúa su camino normal).
 */