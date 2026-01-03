package com.hust.project1.controller;

import com.hust.project1.entity.Member;
import com.hust.project1.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * Display member list with search and pagination
     */
    @GetMapping
    public String listMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String memberCode,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Member> memberPage = memberService.searchMembers(fullName, memberCode, email, phone, status, pageable);

        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberPage.getTotalPages());
        model.addAttribute("totalItems", memberPage.getTotalElements());
        model.addAttribute("pageSize", size);

        // Keep search parameters
        model.addAttribute("searchFullName", fullName != null ? fullName : "");
        model.addAttribute("searchMemberCode", memberCode != null ? memberCode : "");
        model.addAttribute("searchEmail", email != null ? email : "");
        model.addAttribute("searchPhone", phone != null ? phone : "");
        model.addAttribute("searchStatus", status != null ? status : "");

        // Add status enum values for dropdown
        model.addAttribute("statuses", Member.MemberStatus.values());

        return "members";
    }

    /**
     * Show create member form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("member", new Member());
        model.addAttribute("isEdit", false);
        model.addAttribute("statuses", Member.MemberStatus.values());

        return "member-form";
    }

    /**
     * Create new member
     */
    @PostMapping
    public String createMember(@Valid @ModelAttribute Member member,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Check member code uniqueness
        if (memberService.existsByMemberCode(member.getMemberCode())) {
            result.rejectValue("memberCode", "error.member", "Mã độc giả đã tồn tại!");
        }

        // Check email uniqueness
        if (memberService.existsByEmail(member.getEmail())) {
            result.rejectValue("email", "error.member", "Email đã được sử dụng!");
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("statuses", Member.MemberStatus.values());
            return "member-form";
        }

        memberService.save(member);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm độc giả mới thành công!");
        return "redirect:/members";
    }

    /**
     * Show edit member form
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Member> member = memberService.findById(id);

        if (member.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy độc giả!");
            return "redirect:/members";
        }

        model.addAttribute("member", member.get());
        model.addAttribute("isEdit", true);
        model.addAttribute("statuses", Member.MemberStatus.values());

        return "member-form";
    }

    /**
     * Update member
     */
    @PostMapping("/{id}")
    public String updateMember(@PathVariable Long id,
            @Valid @ModelAttribute Member member,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Get existing member to preserve dates
        Optional<Member> existingMemberOpt = memberService.findById(id);
        if (existingMemberOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy độc giả!");
            return "redirect:/members";
        }

        Member existingMember = existingMemberOpt.get();

        // Check member code uniqueness (excluding current member)
        if (memberService.existsByMemberCodeAndIdNot(member.getMemberCode(), id)) {
            result.rejectValue("memberCode", "error.member", "Mã độc giả đã tồn tại!");
        }

        // Check email uniqueness (excluding current member)
        if (memberService.existsByEmailAndIdNot(member.getEmail(), id)) {
            result.rejectValue("email", "error.member", "Email đã được sử dụng!");
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("statuses", Member.MemberStatus.values());
            return "member-form";
        }

        // Preserve dates if not provided
        if (member.getRegistrationDate() == null) {
            member.setRegistrationDate(existingMember.getRegistrationDate());
        }
        if (member.getExpiryDate() == null) {
            member.setExpiryDate(existingMember.getExpiryDate());
        }

        member.setId(id);
        memberService.save(member);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật độc giả thành công!");
        return "redirect:/members";
    }

    /**
     * Delete member
     */
    @PostMapping("/{id}/delete")
    public String deleteMember(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa độc giả thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa độc giả. Độc giả có thể đang có sách mượn.");
        }
        return "redirect:/members";
    }
}
