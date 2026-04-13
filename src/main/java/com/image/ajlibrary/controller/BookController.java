package com.image.ajlibrary.controller;

import com.image.ajlibrary.dto.BookRequest;
import com.image.ajlibrary.entity.Book;
import com.image.ajlibrary.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * POST /api/books
     * Add a new book to the library.
     */
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody BookRequest request) {
        Book book = bookService.addBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    /**
     * GET /api/books
     * Get all books. Optionally filter by title or author query param.
     */
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {

        if (title != null && !title.isBlank()) {
            return ResponseEntity.ok(bookService.searchByTitle(title));
        }
        if (author != null && !author.isBlank()) {
            return ResponseEntity.ok(bookService.searchByAuthor(author));
        }
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    /**
     * GET /api/books/available
     * Get only books with at least 1 available copy.
     */
    @GetMapping("/available")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * PUT /api/books/{id}
     * Update an existing book.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    /**
     * DELETE /api/books/{id}
     * Delete an existing book.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
