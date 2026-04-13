package com.image.ajlibrary.repository;

import com.image.ajlibrary.entity.BorrowRecord;
import com.image.ajlibrary.entity.BorrowRecord.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByUserId(Long userId);

    List<BorrowRecord> findByUserIdAndStatus(Long userId, BorrowStatus status);

    Optional<BorrowRecord> findByUserIdAndBookIdAndStatus(Long userId, Long bookId, BorrowStatus status);

    List<BorrowRecord> findByStatus(BorrowStatus status);

    // Find all overdue unreturned records
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'ISSUED' AND br.dueDate < :today")
    List<BorrowRecord> findOverdueRecords(LocalDate today);

    void deleteByBookId(Long bookId);
}
