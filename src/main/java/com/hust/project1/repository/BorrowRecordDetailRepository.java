package com.hust.project1.repository;

import com.hust.project1.entity.BookCopy;
import com.hust.project1.entity.BorrowRecord;
import com.hust.project1.entity.BorrowRecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BorrowRecordDetailRepository extends JpaRepository<BorrowRecordDetail, Long> {

    /**
     * Find details by borrow record
     */
    @Query("SELECT b FROM BorrowRecordDetail b WHERE b.borrowRecord = :borrowRecord")
    List<BorrowRecordDetail> findByBorrowRecord(BorrowRecord borrowRecord);

    /**
     * Find details by borrow record ID
     */
    List<BorrowRecordDetail> findByBorrowRecordId(Long borrowRecordId);

    /**
     * Sum of all unpaid fines (for financial statistics)
     */
    @Query("SELECT COALESCE(SUM(brd.fine), 0) FROM BorrowRecordDetail brd WHERE brd.fine > 0")
    BigDecimal sumUnpaidFines();

    /**
     * Sum of paid fines (extracted from notes containing payment confirmation)
     * This is an approximation - we look for records with payment notes
     * In a real system, we would have a separate payment_history table
     */
    @Query("SELECT COUNT(brd) FROM BorrowRecordDetail brd " +
            "WHERE brd.notes LIKE '%Đã thanh toán tiền phạt%'")
    Long countPaidFines();

    /**
     * Sum of all fines ever recorded (for financial statistics)
     */
    @Query("SELECT COALESCE(SUM(brd.fine), 0) FROM BorrowRecordDetail brd")
    BigDecimal sumAllFines();

    /**
     * Calculate approximate paid fines
     * Since we don't store payment history, we estimate based on:
     * - Records with payment notes and fine = 0
     * - Assume average fine of 15,000 VND per paid record
     */
    @Query("SELECT COALESCE(COUNT(brd) * 15000, 0) FROM BorrowRecordDetail brd " +
            "WHERE brd.fine = 0 AND brd.notes LIKE '%Đã thanh toán tiền phạt%'")
    BigDecimal sumPaidFines();

    /**
     * Find borrow history of a book copy
     */
    List<BorrowRecordDetail> findByBookCopy(BookCopy bookCopy);

    /**
     * Find borrow history by book copy ID
     */
    List<BorrowRecordDetail> findByBookCopyId(Long bookCopyId);

    /**
     * Count items in a borrow record (for business rule enforcement: 1-3 books)
     */
    long countByBorrowRecordId(Long borrowRecordId);
}
