package com.hust.project1.controller;

import com.hust.project1.entity.BookCopy;
import com.hust.project1.entity.BorrowRecord;
import com.hust.project1.entity.BorrowRecordDetail;
import com.hust.project1.entity.Member;
import com.hust.project1.service.BookCopyService;
import com.hust.project1.service.BorrowRecordService;
import com.hust.project1.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/borrow-records")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BookCopyService bookCopyService;

    /**
     * Display borrow record list with search and pagination
     */
    @GetMapping
    public String listBorrowRecords(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Update overdue status before displaying records
        borrowRecordService.updateOverdueStatus();

        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        Page<BorrowRecord> borrowRecords = borrowRecordService.searchBorrowRecords(
                memberId, status, fromDate, toDate, pageable);

        model.addAttribute("borrowRecords", borrowRecords);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", borrowRecords.getTotalPages());
        model.addAttribute("searchMemberId", memberId != null ? memberId : "");
        model.addAttribute("searchStatus", status != null ? status : "");
        model.addAttribute("searchFromDate", fromDate);
        model.addAttribute("searchToDate", toDate);

        // Add status enum values
        model.addAttribute("statuses", BorrowRecord.RecordStatus.values());

        // Get all members for search dropdown
        List<Member> members = memberService.findAll(PageRequest.of(0, 1000)).getContent();
        model.addAttribute("members", members);

        return "borrow-records";
    }

    /**
     * View borrow record details
     */
    @GetMapping("/{id}")
    public String viewBorrowRecordDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<BorrowRecord> recordOpt = borrowRecordService.findById(id);

        if (recordOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy phiếu mượn!");
            return "redirect:/borrow-records";
        }

        BorrowRecord record = recordOpt.get();
        List<BorrowRecordDetail> details = borrowRecordService.getBorrowRecordDetails(id);
        BigDecimal totalFine = borrowRecordService.calculateTotalFine(id);

        model.addAttribute("record", record);
        model.addAttribute("details", details);
        model.addAttribute("totalFine", totalFine);
        model.addAttribute("copyStatuses", BookCopy.CopyStatus.values());

        return "borrow-record-detail";
    }

    /**
     * Show create borrow record form (simplified - no pagination)
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {

        // Get all active members (activeBorrowCount already in database)
        Page<Member> memberPage = memberService.searchMembers(null, null, null, null, "ACTIVE",
                PageRequest.of(0, 1000));

        // Get all available book copies (load all for grid display with client-side
        // search)
        Page<BookCopy> bookCopyPage = bookCopyService.searchBookCopies(null, null, "AVAILABLE", null,
                PageRequest.of(0, 1000));

        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("bookCopies", bookCopyPage.getContent());

        return "borrow-record-form";
    }

    /**
     * Create new borrow record
     */
    @PostMapping
    public String createBorrowRecord(
            @RequestParam Long memberId,
            @RequestParam List<Long> bookCopyIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            RedirectAttributes redirectAttributes) {

        try {
            BorrowRecord record = borrowRecordService.createBorrowRecord(memberId, bookCopyIds, dueDate);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tạo phiếu mượn thành công! Mã phiếu: " + record.getId());
            return "redirect:/borrow-records/" + record.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/borrow-records/new";
        }
    }

    /**
     * Return all books in record
     */
    @PostMapping("/{id}/return-all")
    public String returnAllBooks(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.returnAllBooks(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã trả toàn bộ sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/borrow-records/" + id;
    }

    /**
     * Return single book
     */
    @PostMapping("/{id}/details/{detailId}/return")
    public String returnSingleBook(@PathVariable Long id, @PathVariable Long detailId,
            RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.returnSingleBook(detailId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã trả sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/borrow-records/" + id;
    }

    /**
     * Update book copy status (mark as lost or damaged)
     */
    @PostMapping("/{id}/details/{detailId}/update-status")
    public String updateBookCopyStatus(@PathVariable Long id, @PathVariable Long detailId,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            BookCopy.CopyStatus newStatus = BookCopy.CopyStatus.valueOf(status);
            borrowRecordService.updateBookCopyStatus(detailId, newStatus);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã cập nhật trạng thái sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/borrow-records/" + id;
    }

    /**
     * Delete borrow record
     */
    @PostMapping("/{id}/delete")
    public String deleteBorrowRecord(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa phiếu mượn thành công!");
            return "redirect:/borrow-records";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/borrow-records/" + id;
        }
    }

    /**
     * Pay fine - mark all fines as paid
     */
    @PostMapping("/{id}/pay-fine")
    public String payFine(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.payFine(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xác nhận thanh toán tiền phạt!");
            return "redirect:/borrow-records/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/borrow-records/" + id;
        }
    }
}
