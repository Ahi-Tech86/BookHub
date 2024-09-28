package com.ahicode.api.services;

import com.ahicode.api.dtos.BookCreationRequestDto;
import com.ahicode.api.dtos.BookDto;
import com.ahicode.api.factories.BookDtoFactory;
import com.ahicode.api.factories.BookEntityFactory;
import com.ahicode.api.services.interfaces.BookService;
import com.ahicode.exceptions.AppException;
import com.ahicode.storage.entities.AuthorEntity;
import com.ahicode.storage.entities.BookEntity;
import com.ahicode.storage.enums.BookGenre;
import com.ahicode.storage.repositories.AuthorRepository;
import com.ahicode.storage.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookDtoFactory bookDtoFactory;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookEntityFactory bookEntityFactory;
    private static final Map<String, BookGenre> genreMap = new HashMap<>();

    static {
        genreMap.put("fantasy", BookGenre.FICTION_FANTASY_GENRE);
        genreMap.put("detectives", BookGenre.FICTION_DETECTIVES_GENRE);
        genreMap.put("horrors", BookGenre.FICTION_HORRORS_GENRE);
        genreMap.put("adventures", BookGenre.FICTION_ADVENTURES_GENRE);
        genreMap.put("poetry", BookGenre.FICTION_POETRY_GENRE);
        genreMap.put("romance", BookGenre.FICTION_ROMANCE_NOVELS_GENRE);
        genreMap.put("novel", BookGenre.FICTION_ROMANCE_NOVELS_GENRE);
        genreMap.put("thrillers", BookGenre.FICTION_THRILLERS_GENRE);
        genreMap.put("comics", BookGenre.FICTION_COMICS_AND_MANGA_GENRE);
        genreMap.put("manga", BookGenre.FICTION_COMICS_AND_MANGA_GENRE);
        genreMap.put("business", BookGenre.NOT_FICTION_BUSINESS_LITERATURE_GENRE);
        genreMap.put("psychology", BookGenre.NOT_FICTION_PSYCHOLOGY_GENRE);
        genreMap.put("art", BookGenre.NOT_FICTION_ARTS_AND_CULTURE_GENRE);
        genreMap.put("culture", BookGenre.NOT_FICTION_ARTS_AND_CULTURE_GENRE);
        genreMap.put("scientific", BookGenre.NOT_FICTION_SCIENTIFIC_GENRE);
        genreMap.put("computer science", BookGenre.NOT_FICTION_COMPUTER_SCIENCE_GENRE);
        genreMap.put("historical", BookGenre.NOT_FICTION_HISTORICAL_GENRE);
        genreMap.put("society", BookGenre.NOT_FICTION_SOCIETY_GENRE);
        genreMap.put("memoirs", BookGenre.NOT_FICTION_MEMOIRS_GENRE);
        genreMap.put("philosophy", BookGenre.NOT_FICTION_PHILOSOPHY_GENRE);
        genreMap.put("religion", BookGenre.NOT_FICTION_RELIGION_GENRE);
    }

    @Override
    @Transactional
    public BookDto createBook(Long authorId, BookCreationRequestDto requestDto) {
        AuthorEntity author = isAuthorExistsById(authorId);
        BookGenre genre = classifyGenre(requestDto.getGenre());

        int birthdate = Integer.parseInt(author.getBirthdate());
        if (requestDto.getPublicationDate() < birthdate) {
            log.error("Attempt to saved book with incorrect publication date");
            throw new AppException("Invalid publication date of book", HttpStatus.BAD_REQUEST);
        }

        BookEntity book = bookEntityFactory.makeBookEntity(requestDto, author, genre);
        BookEntity savedBook = bookRepository.saveAndFlush(book);
        log.info("Book with title: {} was successfully saved", savedBook.getTitle());

        return bookDtoFactory.makeBookDto(savedBook);
    }

    @Override
    public List<BookDto> getAllAuthorsBooks(Long authorId) {
        log.info("Attempt to get all book of author");

        List<BookEntity> booksList = bookRepository.findAllByAuthorId(authorId);

        return booksList.stream()
                .map(bookDtoFactory::makeBookDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto getBook(Long authorId, Long bookId) {
        log.info("Attempt to get book by id {}", bookId);

        BookEntity book = bookRepository.findByIdAndAuthorId(authorId, bookId).orElseThrow(
                () -> new AppException("Book doesn't exists", HttpStatus.NOT_FOUND)
        );

        return bookDtoFactory.makeBookDto(book);
    }

    private AuthorEntity isAuthorExistsById(Long authorId) {
        Optional<AuthorEntity> optionalAuthor = authorRepository.findById(authorId);

        if (optionalAuthor.isEmpty()) {
            log.error("Author doesn't exists with {} id", authorId);
            throw new AppException("Author with id {" + authorId + "} doesn't exists", HttpStatus.NOT_FOUND);
        }

        return optionalAuthor.get();
    }

    private BookGenre classifyGenre(String genreString) {
        return genreMap.get(genreString.toLowerCase());
    }
}

