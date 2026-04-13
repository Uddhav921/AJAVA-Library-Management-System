package com.image.ajlibrary.service;

import com.image.ajlibrary.dto.BorrowResponse;
import com.image.ajlibrary.entity.Book;
import com.image.ajlibrary.entity.BorrowRecord;
import com.image.ajlibrary.entity.BorrowRecord.BorrowStatus;
import com.image.ajlibrary.entity.User;
import com.image.ajlibrary.repository.BorrowRecordRepository;
import com.image.ajlibrary.repository.BookRepository;
import com.image.ajlibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Value("${library.fine.per-day:2}")
    private double finePerDay;

    @Value("${library.loan.period-days:14}")
    private int loanPeriodDays;

    /**
     * Issues a book to a user.
     * - Validates user and book exist
     * - Checks book availability
     * - Checks user doesn't already have this book issued
     * - Decrements available copies
     * - Creates a BorrowRecord with dueDate = today + loanPeriodDays
     */
    @Transactional
    public BorrowResponse issueBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies for: " + book.getTitle());
        }

        // Prevent issuing the same book twice to the same user
        borrowRecordRepository.findByUserIdAndBookIdAndStatus(userId, bookId, BorrowStatus.ISSUED)
                .ifPresent(r -> {
                    throw new IllegalStateException("User already has this book issued (Record ID: " + r.getId() + ")");
                });

        // Decrement available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(loanPeriodDays);

        BorrowRecord record = BorrowRecord.builder()
                .user(user)
                .book(book)
                .issueDate(issueDate)
                .dueDate(dueDate)
                .fine(0.0)
                .status(BorrowStatus.ISSUED)
                .build();

        BorrowRecord saved = borrowRecordRepository.save(record);
        return toResponse(saved);
    }

    /**
     * Returns a book.
     * - Finds the borrow record
     * - Calculates fine if overdue
     * - Updates record status and returnDate
     * - Increments available copies
     */
    @Transactional
    public BorrowResponse returnBook(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Borrow record not found: " + recordId));

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new IllegalStateException("Book has already been returned for record: " + recordId);
        }

        LocalDate returnDate = LocalDate.now();
        double fine = calculateFine(record.getDueDate(), returnDate);

        record.setReturnDate(returnDate);
        record.setFine(fine);
        record.setStatus(BorrowStatus.RETURNED);
        borrowRecordRepository.save(record);

        // Increment available copies
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return toResponse(record);
    }

    /**
     * Fine = max(0, overdueDays) * finePerDay
     * Fine is 0 if returned on or before dueDate.
     */
    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return Math.max(0, overdueDays) * finePerDay;
    }

    @Transactional(readOnly = true)
    public List<BorrowResponse> getBorrowHistory(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return borrowRecordRepository.findByUserId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowResponse> getActiveBorrows(Long userId) {
        return borrowRecordRepository.findByUserIdAndStatus(userId, BorrowStatus.ISSUED)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BorrowResponse> getAllOverdueRecords() {
        return borrowRecordRepository.findOverdueRecords(LocalDate.now())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // -------------------------------------------------------
    // Mapper: BorrowRecord → BorrowResponse
    // -------------------------------------------------------
    private BorrowResponse toResponse(BorrowRecord r) {
        LocalDate today = LocalDate.now();
        boolean overdue = r.getStatus() == BorrowStatus.ISSUED && today.isAfter(r.getDueDate());

        // For unreturned books, compute live fine preview
        double displayFine = r.getStatus() == BorrowStatus.ISSUED
                ? calculateFine(r.getDueDate(), today)
                : r.getFine();

        return BorrowResponse.builder()
                .recordId(r.getId())
                .userId(r.getUser().getId())
                .username(r.getUser().getUsername())
                .bookId(r.getBook().getId())
                .bookTitle(r.getBook().getTitle())
                .isbn(r.getBook().getIsbn())
                .issueDate(r.getIssueDate())
                .dueDate(r.getDueDate())
                .returnDate(r.getReturnDate())
                .fine(displayFine)
                .status(r.getStatus())
                .overdue(overdue)
                .build();
    }
}
