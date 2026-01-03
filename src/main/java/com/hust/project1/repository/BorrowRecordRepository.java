package com.hust.project1.repository;

import com.hust.project1.entity.BorrowRecord;
import com.hust.project1.entity.BorrowRecord.RecordStatus;
import com.hust.project1.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    /**
     * Find all borrow records for a member
     */
    List<BorrowRecord> findByMember(Member member);

    /**
     * Find borrow records by member ID
     */
    List<BorrowRecord> findByMemberId(Long memberId);

    /**
     * Find records by status
     */
    List<BorrowRecord> findByStatus(RecordStatus status);

    /**
     * Find active borrows for a member
     */
    List<BorrowRecord> findByMemberAndStatus(Member member, RecordStatus status);

    /**
     * Find borrow records by member ID and status
     */
    List<BorrowRecord> findByMemberIdAndStatus(Long memberId, RecordStatus status);

    /**
     * Find overdue records (due date before given date and status is ACTIVE)
     */
    List<BorrowRecord> findByDueDateBeforeAndStatus(LocalDate date, RecordStatus status);

    /**
     * Count active borrows for a member (for business rule enforcement)
     */
    long countByMemberIdAndStatus(Long memberId, RecordStatus status);

    /**
     * Count records by status (for dashboard statistics)
     */
    long countByStatus(RecordStatus status);

    /**
     * Search borrow records with pagination
     */
    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "(:memberId IS NULL OR br.member.id = :memberId) AND " +
            "(:status IS NULL OR :status = '' OR CAST(br.status AS string) = :status) AND " +
            "(:fromDate IS NULL OR br.borrowDate >= :fromDate) AND " +
            "(:toDate IS NULL OR br.borrowDate <= :toDate)")
    Page<BorrowRecord> searchCombined(@Param("memberId") Long memberId,
            @Param("status") String status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);
}
