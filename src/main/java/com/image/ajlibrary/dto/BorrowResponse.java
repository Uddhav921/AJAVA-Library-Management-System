package com.image.ajlibrary.dto;

import com.image.ajlibrary.entity.BorrowRecord.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowResponse {

    private Long recordId;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private String isbn;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private BorrowStatus status;
    private boolean overdue;
}
