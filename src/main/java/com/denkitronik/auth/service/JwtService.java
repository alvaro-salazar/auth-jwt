package com.denkitronik.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Esta clase se encarga de generar, verificar y extraer información de los tokens JWT utilizados para la autenticación
 * en la aplicación. También realiza validaciones para garantizar que los tokens sean válidos y no hayan expirado.
 */
@Component
public class JwtService {

    // Esta es la clave secreta que se utiliza para firmar el token JWT.
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    /**
     * Este método genera un token JWT para un usuario específico y establece el tiempo de expiración en 30 minutos.
     * El token se crea utilizando la función createToken().
     * @param userName Es el nombre de usuario del usuario para el que se va a generar el token JWT
     * @return El token JWT generado
     */
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    /**
     *  Este método crea un token JWT para el usuario especificado. Configura las reclamaciones (claims) personalizadas
     *  (si las hay), establece el nombre de usuario como sujeto, la fecha de emisión y la fecha de expiración
     *  en 30 minutos a partir del momento actual. Luego, firma el token utilizando una clave secreta y el algoritmo
     *  de firma HS256.
     * @param claims Son los datos que se van a guardar en el token JWT (por ejemplo, el rol del usuario)
     * @param userName Es el nombre de usuario del usuario para el que se va a generar el token JWT
     * @return El token JWT generado
     */
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Este método obtiene la clave secreta utilizada para firmar el token JWT. La clave se obtiene decodificando
     * una cadena codificada en base64 y se utiliza para firmar y verificar la autenticidad del token.
     * @return La clave secreta utilizada para firmar el token JWT
     */
    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Este método extrae el nombre de usuario del token JWT y lo devuelve.
     * @param token Es el token JWT del que se extrae el nombre de usuario
     * @return El nombre de usuario extraído del token JWT
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Este método extrae la fecha de expiración del token JWT y la devuelve como un objeto Date.
     * @param token Es el token JWT del que se extrae la fecha de expiración
     * @return La fecha de expiración extraída del token JWT
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     *  Este método permite extraer otras reclamaciones personalizadas del token JWT utilizando una función de
     *  resolución de reclamaciones personalizada.
     * @param token Es el token JWT del que se extraen las reclamaciones personalizadas (por ejemplo, el rol del usuario)
     * @param claimsResolver Es una función de resolución de reclamaciones personalizada que se utiliza para extraer
     *                       las reclamaciones personalizadas del token JWT
     * @return Las reclamaciones personalizadas extraídas del token JWT
     * @param <T> Es el tipo de reclamación personalizada que se extrae del token JWT (por ejemplo, el rol del usuario)
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Este método extrae todas las reclamaciones del token JWT y las devuelve como un objeto Claims (reclamaciones).
     * @param token Es el token JWT del que se extraen todas las reclamaciones (por ejemplo, el rol del usuario)
     * @return Todas las reclamaciones extraídas del token JWT
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Este método verifica si el token JWT ha expirado comparando la fecha de expiración extraída del token con la
     * fecha actual.
     * @param token Es el token JWT que se va a verificar si ha expirado o no
     * @return true si el token JWT ha expirado, false en caso contrario
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     *  Este método valida el token JWT verificando si el nombre de usuario extraído del token coincide con el nombre
     *  de usuario proporcionado en los detalles del usuario y si el token no ha expirado. Retorna true si el token es
     *  válido y false en caso contrario.
     * @param token Es el token JWT que se va a validar si es válido o no
     * @param userDetails Son los detalles del usuario que se utilizan para validar el token JWT
     * @return true si el token JWT es válido, false en caso contrario
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
