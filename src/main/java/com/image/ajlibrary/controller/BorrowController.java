package com.image.ajlibrary.controller;

import com.image.ajlibrary.dto.BorrowResponse;
import com.image.ajlibrary.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    /**
     * POST /api/borrow/issue?userId=1&bookId=2
     * Issue a book to a user.
     */
    @PostMapping("/issue")
    public ResponseEntity<BorrowResponse> issueBook(
            @RequestParam Long userId,
            @RequestParam Long bookId) {
        BorrowResponse response = borrowService.issueBook(userId, bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/borrow/return/{recordId}
     * Return a book; fine is calculated automatically.
     */
    @PutMapping("/return/{recordId}")
    public ResponseEntity<BorrowResponse> returnBook(@PathVariable Long recordId) {
        BorrowResponse response = borrowService.returnBook(recordId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/borrow/history/{userId}
     * Full borrow history for a user (issued + returned).
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<BorrowResponse>> getBorrowHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowService.getBorrowHistory(userId));
    }

    /**
     * GET /api/borrow/active/{userId}
     * Active (currently issued) books for a user.
     * Live fine preview is included in the response.
     */
    @GetMapping("/active/{userId}")
    public ResponseEntity<List<BorrowResponse>> getActiveBorrows(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowService.getActiveBorrows(userId));
    }

    /**
     * GET /api/borrow/overdue
     * All overdue unreturned records across all users (admin view).
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowResponse>> getAllOverdueRecords() {
        return ResponseEntity.ok(borrowService.getAllOverdueRecords());
    }
}
