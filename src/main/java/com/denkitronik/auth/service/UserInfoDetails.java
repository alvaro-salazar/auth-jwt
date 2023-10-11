package com.denkitronik.auth.service;

import com.denkitronik.auth.entity.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserInfoDetails se utiliza para adaptar un objeto UserInfo en un objeto UserDetails que Spring Security puede
 * utilizar para autenticar y autorizar al usuario.
 */
public class UserInfoDetails implements UserDetails {

    private String name;
    private String password;
    private List<GrantedAuthority> authorities; // Almacena las autoridades (roles) del usuario como una lista de objetos GrantedAuthority.

    // Constructor para convertir un objeto UserInfo en un objeto UserDetails que Spring Security puede utilizar
    // internamente para autenticar y autorizar al usuario.

    /**
     * Este constructor se utiliza para convertir un objeto UserInfo en un objeto UserDetails que Spring Security
     * puede utilizar para autenticar y autorizar al usuario.
     * @param userInfo Es el objeto UserInfo que se va a convertir en un objeto UserDetails
     */
    public UserInfoDetails(UserInfo userInfo) {
        name = userInfo.getName();
        password = userInfo.getPassword();
        authorities = Arrays.stream(userInfo.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    // Implementa el método de la interfaz UserDetails que devuelve una colección de autoridades (roles) que tiene
    // el usuario. En este caso, la colección se compone de objetos GrantedAuthority que representan los roles del usuario.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
