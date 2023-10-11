package com.denkitronik.auth.repository;
import com.denkitronik.auth.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Se utiliza para realizar operaciones de base de datos relacionadas con la entidad UserInfo, como buscar usuarios
 * por su nombre de usuario. Spring Data JPA proporciona implementaciones de métodos comunes como findById, save,
 * findAll, etc.
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByName(String username);
}
