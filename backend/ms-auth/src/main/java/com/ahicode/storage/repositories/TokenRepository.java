package com.ahicode.storage.repositories;

import com.ahicode.storage.entities.RefreshTokenEntity;
import com.ahicode.storage.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByUser(UserEntity user);

    @Query("SELECT rt FROM RefreshTokenEntity rt WHERE rt.user.email = :email")
    Optional<RefreshTokenEntity> findByEmail(@Param("email") String email);
}
