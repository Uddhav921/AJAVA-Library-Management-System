package com.image.ajlibrary.controller;

import com.image.ajlibrary.dto.BorrowResponse;
import com.image.ajlibrary.dto.LibraryStatsDto;
import com.image.ajlibrary.entity.BorrowRecord.BorrowStatus;
import com.image.ajlibrary.repository.BookRepository;
import com.image.ajlibrary.repository.BorrowRecordRepository;
import com.image.ajlibrary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CO1 - Servlet & JSP API.
 *
 * This is a traditional Spring MVC @Controller (NOT @RestController).
 * It returns logical view names that the InternalResourceViewResolver
 * resolves to JSP files under /WEB-INF/views/.
 *
 * The @Controller + ViewResolver combination demonstrates how modern
 * Spring Boot bridges the Servlet/JSP paradigm with the framework layer.
 *
 * Demonstrates:
 *  - View resolution via Spring MVC (backed by DispatcherServlet → JSP)
 *  - JSTL expressions used inside the JSP template
 *  - Model attributes injected into the JSP page scope (like request attributes
 *    manually set via request.setAttribute() in raw Servlet programming)
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    /**
     * GET /admin/report
     * Renders the admin_report.jsp page with live library statistics.
     * Session/Cookie check note: In a full implementation, session-based
     * ADMIN role check would go here (or via Spring Security).
     */
    @GetMapping("/report")
    public String adminReport(Model model) {

        // ---- Aggregate statistics (CO4: JPA queries on normalized tables) ----
        long totalBooks   = bookRepository.count();
        long totalUsers   = userRepository.count();
        long activeBorrow = borrowRecordRepository.findByStatus(BorrowStatus.ISSUED).size();
        long returned     = borrowRecordRepository.findByStatus(BorrowStatus.RETURNED).size();

        List<BorrowResponse> overdueList = borrowRecordRepository
                .findOverdueRecords(LocalDate.now())
                .stream()
                .map(r -> {
                    long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(r.getDueDate(), LocalDate.now());
                    return BorrowResponse.builder()
                            .recordId(r.getId())
                            .userId(r.getUser().getId())
                            .username(r.getUser().getUsername())
                            .bookId(r.getBook().getId())
                            .bookTitle(r.getBook().getTitle())
                            .isbn(r.getBook().getIsbn())
                            .issueDate(r.getIssueDate())
                            .dueDate(r.getDueDate())
                            .fine(overdueDays * 5.0)
                            .finePaid(r.getFinePaid())
                            .status(r.getStatus())
                            .overdue(true)
                            .build();
                })
                .collect(Collectors.toList());

        double totalFinesPending = overdueList.stream()
                .filter(b -> !Boolean.TRUE.equals(b.getFinePaid()))
                .mapToDouble(BorrowResponse::getFine)
                .sum();

        double totalFinesCollected = borrowRecordRepository
                .findByStatus(BorrowStatus.RETURNED)
                .stream()
                .mapToDouble(r -> r.getFine())
                .sum();

        LibraryStatsDto stats = LibraryStatsDto.builder()
                .totalBooks(totalBooks)
                .totalUsers(totalUsers)
                .activeBorrows(activeBorrow)
                .overdueCount(overdueList.size())
                .returnedRecords(returned)
                .totalFinesCollected(totalFinesCollected)
                .totalFinesPending(totalFinesPending)
                .build();

        // Inject into Model → available in JSP as ${stats} and ${overdueList}
        model.addAttribute("stats", stats);
        model.addAttribute("overdueList", overdueList);
        model.addAttribute("generatedAt", java.time.LocalDateTime.now().toString());

        // Returns logical view name → resolved to /WEB-INF/views/admin/report.jsp
        return "admin/report";
    }
}
