package com.ahicode.storage.repositories;

import com.ahicode.storage.entities.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findById(Long id);

    @Query("SELECT be FROM BookEntity be WHERE be.author.id = :authorId")
    List<BookEntity> findAllByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT be FROM BookEntity be WHERE be.author.id = :authorId AND be.id = :id")
    Optional<BookEntity> findByIdAndAuthorId(@Param("authorId") Long authorId, @Param("id") Long id);
}
