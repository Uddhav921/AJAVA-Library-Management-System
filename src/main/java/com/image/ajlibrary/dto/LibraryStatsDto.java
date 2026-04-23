package com.image.ajlibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to carry library-wide statistics to the JSP admin report page.
 * Demonstrates OOP (CO2): encapsulated POJO passed via Model to the JSP view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryStatsDto {

    private long totalBooks;
    private long totalUsers;
    private long activeBorrows;
    private long overdueCount;
    private long returnedRecords;
    private double totalFinesCollected;
    private double totalFinesPending;
}
