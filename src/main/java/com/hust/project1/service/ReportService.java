package com.hust.project1.service;

import com.hust.project1.entity.Book;
import com.hust.project1.entity.BookCopy;
import com.hust.project1.entity.BorrowRecord;
import com.hust.project1.entity.Member;
import com.hust.project1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BorrowRecordDetailRepository borrowRecordDetailRepository;

    /**
     * Get overview statistics
     */
    public Map<String, Object> getOverviewStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total books (distinct titles)
        long totalBooks = bookRepository.count();
        stats.put("totalBooks", totalBooks);

        // Total book copies (physical items)
        long totalCopies = bookCopyRepository.count();
        stats.put("totalCopies", totalCopies);

        // Currently borrowed books
        long borrowedBooks = bookCopyRepository.countByStatus(BookCopy.CopyStatus.BORROWED);
        stats.put("borrowedBooks", borrowedBooks);

        // Overdue books
        long overdueBooks = borrowRecordRepository.countByStatus(BorrowRecord.RecordStatus.OVERDUE);
        stats.put("overdueBooks", overdueBooks);

        // Active members
        long activeMembers = memberRepository.countByStatus(Member.MemberStatus.ACTIVE);
        stats.put("activeMembers", activeMembers);

        return stats;
    }

    /**
     * Get book statistics
     */
    public Map<String, Object> getBookStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Top 10 most borrowed books
        Pageable top10 = PageRequest.of(0, 10);
        List<Object[]> topBorrowedRaw = bookRepository.findTopBorrowedBooks(top10);
        List<Map<String, Object>> topBorrowed = new ArrayList<>();
        for (Object[] row : topBorrowedRaw) {
            Map<String, Object> item = new HashMap<>();
            item.put("book", row[0]);
            item.put("borrowCount", row[1]);
            topBorrowed.add(item);
        }
        stats.put("topBorrowedBooks", topBorrowed);

        // Never borrowed books
        List<Book> neverBorrowed = bookRepository.findNeverBorrowedBooks();
        stats.put("neverBorrowedBooks", neverBorrowed);

        // Damaged/Lost books
        List<BookCopy.CopyStatus> problemStatuses = Arrays.asList(
                BookCopy.CopyStatus.DAMAGED,
                BookCopy.CopyStatus.LOST);
        List<BookCopy> problemBooks = bookCopyRepository.findByStatusIn(problemStatuses);
        stats.put("damagedLostBooks", problemBooks);

        // Count by status
        long damagedCount = bookCopyRepository.countByStatus(BookCopy.CopyStatus.DAMAGED);
        long lostCount = bookCopyRepository.countByStatus(BookCopy.CopyStatus.LOST);
        stats.put("damagedCount", damagedCount);
        stats.put("lostCount", lostCount);

        return stats;
    }

    /**
     * Get member statistics
     */
    public Map<String, Object> getMemberStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Top 10 active members
        Pageable top10 = PageRequest.of(0, 10);
        List<Object[]> topMembersRaw = memberRepository.findTopActiveMembers(top10);
        List<Map<String, Object>> topMembers = new ArrayList<>();
        for (Object[] row : topMembersRaw) {
            Map<String, Object> item = new HashMap<>();
            item.put("member", row[0]);
            item.put("borrowCount", row[1]);
            topMembers.add(item);
        }
        stats.put("topActiveMembers", topMembers);

        // Blacklist members (with fines or overdue)
        List<Member> blacklist = memberRepository.findBlacklistMembers();
        stats.put("blacklistMembers", blacklist);

        return stats;
    }

    /**
     * Get financial statistics
     */
    public Map<String, Object> getFinancialStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total unpaid fines (current fines > 0)
        BigDecimal unpaidFines = borrowRecordDetailRepository.sumUnpaidFines();
        stats.put("unpaidFines", unpaidFines != null ? unpaidFines : BigDecimal.ZERO);

        // Calculate paid fines by finding records with payment notes
        // When a fine is paid, we set fine to 0 and add "Đã thanh toán tiền phạt" to
        // notes
        // So we need to count records that have this note
        BigDecimal paidFines = borrowRecordDetailRepository.sumPaidFines();
        stats.put("paidFines", paidFines != null ? paidFines : BigDecimal.ZERO);

        // Total all fines (paid + unpaid)
        BigDecimal totalFines = (unpaidFines != null ? unpaidFines : BigDecimal.ZERO)
                .add(paidFines != null ? paidFines : BigDecimal.ZERO);
        stats.put("totalFines", totalFines);

        return stats;
    }

    /**
     * Get all statistics at once
     */
    public Map<String, Object> getAllStatistics() {
        Map<String, Object> allStats = new HashMap<>();
        allStats.put("overview", getOverviewStatistics());
        allStats.put("books", getBookStatistics());
        allStats.put("members", getMemberStatistics());
        allStats.put("financial", getFinancialStatistics());
        return allStats;
    }
}
