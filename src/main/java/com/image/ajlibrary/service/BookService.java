package com.image.ajlibrary.service;

import com.image.ajlibrary.dto.BookRequest;
import com.image.ajlibrary.entity.Book;
import com.image.ajlibrary.repository.BookRepository;
import com.image.ajlibrary.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    @Transactional
    public Book addBook(BookRequest request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new IllegalArgumentException("Book with ISBN '" + request.getIsbn() + "' already exists.");
        }

        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .build();

        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0);
    }

    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    /**
     * Called internally by BorrowService — decrements available copies.
     */
    @Transactional
    public void decrementAvailableCopies(Book book) {
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies available for book: " + book.getTitle());
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    /**
     * Called internally by BorrowService — increments available copies on return.
     */
    @Transactional
    public void incrementAvailableCopies(Book book) {
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Long id, BookRequest request) {
        Book book = getBookById(id);
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        // Simple logic for copies: update total and available equally scaled
        int diff = request.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));

        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = getBookById(id);
        borrowRecordRepository.deleteByBookId(id);
        bookRepository.delete(book);
    }
}
