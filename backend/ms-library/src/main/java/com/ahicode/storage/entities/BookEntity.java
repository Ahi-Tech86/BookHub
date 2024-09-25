package com.ahicode.storage.entities;

import com.ahicode.storage.enums.BookGenre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    private String description;

    @Column(name = "publication_date")
    private String publicationDate;
}
