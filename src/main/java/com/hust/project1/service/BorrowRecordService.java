package com.hust.project1.service;

import com.hust.project1.entity.BookCopy;
import com.hust.project1.entity.BorrowRecord;
import com.hust.project1.entity.BorrowRecordDetail;
import com.hust.project1.entity.Member;
import com.hust.project1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BorrowRecordDetailRepository borrowRecordDetailRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private MemberRepository memberRepository;

    // Fine rates
    private static final BigDecimal LATE_FINE_PER_DAY = new BigDecimal("5000"); // 5,000 VND/day
    private static final BigDecimal DAMAGED_FINE = new BigDecimal("50000"); // 50,000 VND
    private static final BigDecimal LOST_FINE = new BigDecimal("100000"); // 100,000 VND

    /**
     * Get all borrow records with pagination
     */
    public Page<BorrowRecord> findAll(Pageable pageable) {
        return borrowRecordRepository.findAll(pageable);
    }

    /**
     * Get borrow record by ID
     */
    public Optional<BorrowRecord> findById(Long id) {
        return borrowRecordRepository.findById(id);
    }

    /**
     * Search borrow records
     */
    public Page<BorrowRecord> searchBorrowRecords(Long memberId, String status,
            LocalDate fromDate, LocalDate toDate,
            Pageable pageable) {
        return borrowRecordRepository.searchCombined(memberId, status, fromDate, toDate, pageable);
    }

    /**
     * Count active borrows by member (for 3-record limit)
     */
    public long countActiveBorrowsByMember(Long memberId) {
        return borrowRecordRepository.countByMemberIdAndStatus(memberId, BorrowRecord.RecordStatus.ACTIVE);
    }

    /**
     * Create new borrow record
     */
    public BorrowRecord createBorrowRecord(Long memberId, List<Long> bookCopyIds, LocalDate dueDate) {
        // Validate member exists
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy độc giả!"));

        // Check book count limit (3 books total)
        int requestedBooks = bookCopyIds.size();
        int currentCount = member.getActiveBorrowCount();

        if (currentCount + requestedBooks > 3) {
            throw new RuntimeException("Vượt quá giới hạn! Độc giả đang mượn " + currentCount +
                    " sách, chỉ có thể mượn thêm " + (3 - currentCount) + " sách.");
        }

        // Create borrow record
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setMember(member);
        borrowRecord.setBorrowDate(LocalDate.now());
        borrowRecord.setDueDate(dueDate != null ? dueDate : LocalDate.now().plusDays(14));
        borrowRecord.setStatus(BorrowRecord.RecordStatus.ACTIVE);

        borrowRecord = borrowRecordRepository.save(borrowRecord);

        // Create borrow record details for each book copy
        for (Long bookCopyId : bookCopyIds) {
            BookCopy bookCopy = bookCopyRepository.findById(bookCopyId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bản sao sách!"));

            // Check if book copy is available
            if (bookCopy.getStatus() != BookCopy.CopyStatus.AVAILABLE) {
                throw new RuntimeException("Bản sao sách " + bookCopy.getCopyNumber() + " không khả dụng!");
            }

            // Update book copy status to BORROWED
            bookCopy.setStatus(BookCopy.CopyStatus.BORROWED);
            bookCopyRepository.save(bookCopy);

            // Create detail
            BorrowRecordDetail detail = new BorrowRecordDetail();
            detail.setBorrowRecord(borrowRecord);
            detail.setBookCopy(bookCopy);
            detail.setFine(BigDecimal.ZERO);
            borrowRecordDetailRepository.save(detail);
        }

        // Increment member's active borrow count
        member.setActiveBorrowCount(currentCount + requestedBooks);
        memberRepository.save(member);

        return borrowRecord;
    }

    /**
     * Return all books in a borrow record
     */
    public void returnAllBooks(Long recordId) {
        BorrowRecord record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn!"));

        List<BorrowRecordDetail> details = borrowRecordDetailRepository.findByBorrowRecordId(recordId);

        for (BorrowRecordDetail detail : details) {
            if (detail.getActualReturnDate() == null) {
                returnSingleBook(detail.getId());
            }
        }

        // Update record status
        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowRecord.RecordStatus.RETURNED);
        borrowRecordRepository.save(record);

        // Note: activeBorrowCount is already decremented in returnSingleBook()
    }

    /**
     * Return single book
     */
    public void returnSingleBook(Long detailId) {
        BorrowRecordDetail detail = borrowRecordDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết phiếu mượn!"));

        if (detail.getActualReturnDate() != null) {
            throw new RuntimeException("Sách đã được trả!");
        }

        // Set return date
        detail.setActualReturnDate(LocalDate.now());

        // Calculate late fine
        BorrowRecord record = detail.getBorrowRecord();
        if (LocalDate.now().isAfter(record.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
            BigDecimal lateFine = LATE_FINE_PER_DAY.multiply(BigDecimal.valueOf(daysLate));
            detail.setFine(detail.getFine().add(lateFine));
        }

        // Update book copy status to AVAILABLE
        BookCopy bookCopy = detail.getBookCopy();
        bookCopy.setStatus(BookCopy.CopyStatus.AVAILABLE);
        bookCopyRepository.save(bookCopy);

        borrowRecordDetailRepository.save(detail);

        // Decrement member's active borrow count
        Member member = record.getMember();
        member.setActiveBorrowCount(Math.max(0, member.getActiveBorrowCount() - 1));
        memberRepository.save(member);

        // Check if all books are returned
        List<BorrowRecordDetail> allDetails = borrowRecordDetailRepository.findByBorrowRecordId(record.getId());
        boolean allReturned = allDetails.stream().allMatch(d -> d.getActualReturnDate() != null);

        if (allReturned) {
            record.setReturnDate(LocalDate.now());
            record.setStatus(BorrowRecord.RecordStatus.RETURNED);
            borrowRecordRepository.save(record);
        }
    }

    /**
     * Update book copy status (mark as lost or damaged)
     */
    public void updateBookCopyStatus(Long detailId, BookCopy.CopyStatus newStatus) {
        BorrowRecordDetail detail = borrowRecordDetailRepository.findById(detailId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết phiếu mượn!"));

        BookCopy bookCopy = detail.getBookCopy();
        bookCopy.setStatus(newStatus);
        bookCopyRepository.save(bookCopy);

        // Calculate fine based on status
        if (newStatus == BookCopy.CopyStatus.LOST) {
            detail.setFine(detail.getFine().add(LOST_FINE));
            detail.setNotes("Sách bị mất");
            detail.setActualReturnDate(LocalDate.now()); // Mark as "returned" (lost)
        } else if (newStatus == BookCopy.CopyStatus.DAMAGED) {
            detail.setFine(detail.getFine().add(DAMAGED_FINE));
            detail.setNotes("Sách bị hỏng");
            detail.setActualReturnDate(LocalDate.now()); // Mark as "returned" (damaged)
        }

        borrowRecordDetailRepository.save(detail);

        // Decrement member's active borrow count (book is no longer "active")
        if (newStatus == BookCopy.CopyStatus.LOST || newStatus == BookCopy.CopyStatus.DAMAGED) {
            BorrowRecord record = detail.getBorrowRecord();
            Member member = record.getMember();
            member.setActiveBorrowCount(Math.max(0, member.getActiveBorrowCount() - 1));
            memberRepository.save(member);
        }
    }

    /**
     * Delete borrow record (with validation)
     */
    public void deleteById(Long id) {
        // Validate that the record exists
        borrowRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn!"));

        // Check if any book is still borrowed
        List<BorrowRecordDetail> details = borrowRecordDetailRepository.findByBorrowRecordId(id);
        boolean hasUnreturnedBooks = details.stream()
                .anyMatch(d -> d.getBookCopy().getStatus() == BookCopy.CopyStatus.BORROWED);

        if (hasUnreturnedBooks) {
            throw new RuntimeException("Không thể xóa! Vẫn còn sách chưa trả.");
        }

        // Check if there are unpaid fines
        BigDecimal totalFine = calculateTotalFine(id);
        if (totalFine.compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Không thể xóa! Vẫn còn tiền phạt chưa thanh toán: " +
                    totalFine + " VND");
        }

        borrowRecordRepository.deleteById(id);
    }

    /**
     * Pay fine - mark all fines as paid (reset to zero)
     */
    public void payFine(Long recordId) {
        // Validate record exists
        borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn!"));

        List<BorrowRecordDetail> details = borrowRecordDetailRepository.findByBorrowRecordId(recordId);

        // Reset all fines to zero
        for (BorrowRecordDetail detail : details) {
            if (detail.getFine().compareTo(BigDecimal.ZERO) > 0) {
                detail.setFine(BigDecimal.ZERO);
                detail.setNotes(
                        (detail.getNotes() != null ? detail.getNotes() + " | " : "") + "Đã thanh toán tiền phạt");
                borrowRecordDetailRepository.save(detail);
            }
        }
    }

    /**
     * Calculate total fine for a borrow record
     */
    public BigDecimal calculateTotalFine(Long recordId) {
        List<BorrowRecordDetail> details = borrowRecordDetailRepository.findByBorrowRecordId(recordId);
        return details.stream()
                .map(BorrowRecordDetail::getFine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get borrow record details
     */
    public List<BorrowRecordDetail> getBorrowRecordDetails(Long recordId) {
        return borrowRecordDetailRepository.findByBorrowRecordId(recordId);
    }

    /**
     * Update overdue status for all active records and calculate fines
     */
    public void updateOverdueStatus() {
        List<BorrowRecord> overdueRecords = borrowRecordRepository
                .findByDueDateBeforeAndStatus(LocalDate.now(), BorrowRecord.RecordStatus.ACTIVE);

        for (BorrowRecord record : overdueRecords) {
            // Update status to OVERDUE
            record.setStatus(BorrowRecord.RecordStatus.OVERDUE);
            borrowRecordRepository.save(record);

            // Calculate and update fines for each unreturned book
            List<BorrowRecordDetail> details = borrowRecordDetailRepository.findByBorrowRecordId(record.getId());
            for (BorrowRecordDetail detail : details) {
                // Only calculate fine for books that haven't been returned
                if (detail.getActualReturnDate() == null) {
                    // Calculate days overdue
                    long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());

                    // Calculate fine (5,000 VND per day)
                    BigDecimal fine = LATE_FINE_PER_DAY.multiply(new BigDecimal(daysOverdue));

                    // Update fine in detail
                    detail.setFine(fine);

                    // Add note about the fine
                    String fineNote = "Quá hạn " + daysOverdue + " ngày - Phạt: " +
                            fine.longValue() + " VND";
                    if (detail.getNotes() != null && !detail.getNotes().isEmpty()) {
                        detail.setNotes(detail.getNotes() + " | " + fineNote);
                    } else {
                        detail.setNotes(fineNote);
                    }

                    borrowRecordDetailRepository.save(detail);
                }
            }
        }

        if (!overdueRecords.isEmpty()) {
            System.out.println("✅ Updated " + overdueRecords.size() + " overdue records and calculated fines");
        }
    }
}
