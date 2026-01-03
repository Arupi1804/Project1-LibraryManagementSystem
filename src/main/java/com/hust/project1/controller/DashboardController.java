package com.hust.project1.controller;

import com.hust.project1.entity.BorrowRecord;
import com.hust.project1.repository.BookRepository;
import com.hust.project1.repository.BorrowRecordRepository;
import com.hust.project1.repository.MemberRepository;
import com.hust.project1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        model.addAttribute("username", username);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", role.equals("ROLE_ADMIN"));

        // Add statistics
        long totalBooks = bookRepository.count();
        long totalMembers = memberRepository.count();
        long activeBorrows = borrowRecordRepository.countByStatus(BorrowRecord.RecordStatus.ACTIVE)
                + borrowRecordRepository.countByStatus(BorrowRecord.RecordStatus.OVERDUE);
        long totalUsers = userRepository.count();

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("activeBorrows", activeBorrows);
        model.addAttribute("totalUsers", totalUsers);

        return "dashboard";
    }
}
