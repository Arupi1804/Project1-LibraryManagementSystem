package com.hust.project1.controller;

import com.hust.project1.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Main reports page with all statistics
     */
    @GetMapping
    public String reportsPage(Model model) {
        // Get all statistics
        Map<String, Object> allStats = reportService.getAllStatistics();

        // Add to model
        model.addAttribute("overview", allStats.get("overview"));
        model.addAttribute("bookStats", allStats.get("books"));
        model.addAttribute("memberStats", allStats.get("members"));
        model.addAttribute("financialStats", allStats.get("financial"));

        return "reports";
    }
}
