package com.ahicode.storage.repositories;

import com.ahicode.storage.entities.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
    Optional<AuthorEntity> findById(Long id);
}
