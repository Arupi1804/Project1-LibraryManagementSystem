package com.hust.project1.repository;

import com.hust.project1.entity.Member;
import com.hust.project1.entity.Member.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

        /**
         * Find member by member code
         */
        Optional<Member> findByMemberCode(String memberCode);

        /**
         * Find member by email
         */
        Optional<Member> findByEmail(String email);

        /**
         * Find members by status
         */
        List<Member> findByStatus(MemberStatus status);

        /**
         * Search members by name (case-insensitive, partial match)
         */
        List<Member> findByFullNameContainingIgnoreCase(String name);

        /**
         * Check if member code exists
         */
        boolean existsByMemberCode(String memberCode);

        /**
         * Check if email exists
         */
        boolean existsByEmail(String email);

        /**
         * Search members with multiple criteria
         */
        @Query("SELECT m FROM Member m WHERE " +
                        "(:memberCode IS NULL OR :memberCode = '' OR m.memberCode LIKE CONCAT('%', :memberCode, '%')) AND "
                        +
                        "(:fullName IS NULL OR :fullName = '' OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :fullName, '%'))) AND "
                        +
                        "(:email IS NULL OR :email = '' OR LOWER(m.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
                        "(:phone IS NULL OR :phone = '' OR m.phone LIKE CONCAT('%', :phone, '%')) AND " +
                        "(:status IS NULL OR :status = '' OR STR(m.status) = :status)")
        Page<Member> searchCombined(@Param("memberCode") String memberCode,
                        @Param("fullName") String fullName,
                        @Param("email") String email,
                        @Param("phone") String phone,
                        @Param("status") String status,
                        Pageable pageable);

        /**
         * Find top active members by borrow count (for statistics)
         */
        @Query("SELECT m, COUNT(br) as borrowCount FROM Member m " +
                        "JOIN BorrowRecord br ON br.member = m " +
                        "GROUP BY m ORDER BY borrowCount DESC")
        List<Object[]> findTopActiveMembers(Pageable pageable);

        /**
         * Find blacklist members (with unpaid fines or overdue books)
         */
        @Query("SELECT DISTINCT m FROM Member m " +
                        "JOIN BorrowRecord br ON br.member = m " +
                        "LEFT JOIN BorrowRecordDetail brd ON brd.borrowRecord = br " +
                        "WHERE brd.fine > 0 OR br.status = 'OVERDUE'")
        List<Member> findBlacklistMembers();

        /**
         * Count members by status (for statistics)
         */
        long countByStatus(Member.MemberStatus status);
}
