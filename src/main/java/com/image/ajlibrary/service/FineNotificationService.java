package com.image.ajlibrary.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * CO2 - Multithreading / @Async demonstration.
 *
 * This service handles non-blocking fine notification tasks.
 * In a production system this would send emails / SMS; here it logs
 * to the console to demonstrate @Async running on a separate thread
 * from the HTTP request thread (library-async-pool-*).
 *
 * The executor name "libraryAsyncExecutor" ties back to AsyncConfig,
 * satisfying the Thread pools requirement of CO2.
 */
@Slf4j
@Service
public class FineNotificationService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Sends an overdue fine alert asynchronously.
     * Runs on the "libraryAsyncExecutor" thread pool (see AsyncConfig).
     *
     * @param username  name of the member who returned late
     * @param bookTitle title of the returned book
     * @param fine      fine amount in INR
     * @param recordId  borrow record ID for reference
     * @return CompletableFuture so callers can optionally wait/chain
     */
    @Async("libraryAsyncExecutor")
    public CompletableFuture<String> sendFineAlert(String username,
                                                    String bookTitle,
                                                    double fine,
                                                    Long recordId) {
        String thread = Thread.currentThread().getName();
        String timestamp = LocalDateTime.now().format(FORMATTER);

        log.warn("=======================================================");
        log.warn("[FINE ALERT] Thread  : {}", thread);
        log.warn("[FINE ALERT] Time    : {}", timestamp);
        log.warn("[FINE ALERT] Member  : {}", username);
        log.warn("[FINE ALERT] Book    : {}", bookTitle);
        log.warn("[FINE ALERT] Fine    : INR {}", String.format("%.2f", fine));
        log.warn("[FINE ALERT] Record# : {}", recordId);
        log.warn("[FINE ALERT] Status  : Notification dispatched (would send email/SMS in production)");
        log.warn("=======================================================");

        // Simulate slight processing delay (e.g., external email API call)
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String message = String.format(
                "Fine alert for %s: INR %.2f for book '%s' (Record #%d)", username, fine, bookTitle, recordId);
        return CompletableFuture.completedFuture(message);
    }

    /**
     * Sends a welcome / book-issued confirmation asynchronously.
     * Demonstrates async for success scenarios too, not just fines.
     */
    @Async("libraryAsyncExecutor")
    public void sendBookIssuedConfirmation(String username, String bookTitle, String dueDate) {
        String thread = Thread.currentThread().getName();
        log.info("[BOOK ISSUED] Thread  : {}", thread);
        log.info("[BOOK ISSUED] Member  : {}", username);
        log.info("[BOOK ISSUED] Book    : {}", bookTitle);
        log.info("[BOOK ISSUED] Due by  : {}", dueDate);
        log.info("[BOOK ISSUED] Status  : Confirmation sent (async)");
    }
}
