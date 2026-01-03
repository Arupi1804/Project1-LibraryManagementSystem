package com.hust.project1.controller;

import com.hust.project1.entity.User;
import com.hust.project1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * List all users with search and pagination
     */
    @GetMapping
    public String listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users = userService.searchUsers(keyword, pageable);

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("searchKeyword", keyword != null ? keyword : "");
        model.addAttribute("currentUsername", authentication.getName());
        model.addAttribute("roles", User.UserRole.values());

        return "users";
    }

    /**
     * Show create user form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("roles", User.UserRole.values());
        return "user-form";
    }

    /**
     * Create new user
     */
    @PostMapping
    public String createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            @RequestParam User.UserRole role,
            RedirectAttributes redirectAttributes) {

        try {
            userService.createUser(username, password, email, role);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Tạo tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Lỗi: " + e.getMessage());
            return "redirect:/users/new";
        }

        return "redirect:/users";
    }

    /**
     * Toggle user enabled/disabled status
     */
    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.toggleUserStatus(id, authentication.getName());
            String status = user.getEnabled() ? "mở khoá" : "khoá";
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Đã " + status + " tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Lỗi: " + e.getMessage());
        }

        return "redirect:/users";
    }

    /**
     * Change user role
     */
    @PostMapping("/{id}/change-role")
    public String changeRole(
            @PathVariable Long id,
            @RequestParam User.UserRole role,
            RedirectAttributes redirectAttributes) {

        try {
            userService.changeUserRole(id, role);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Đã thay đổi vai trò thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Lỗi: " + e.getMessage());
        }

        return "redirect:/users";
    }

    /**
     * Delete user
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            userService.deleteUser(id, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Đã xóa tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Lỗi: " + e.getMessage());
        }

        return "redirect:/users";
    }

    /**
     * Show user detail page
     */
    @GetMapping("/{id}")
    public String showUserDetail(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        model.addAttribute("user", user);
        model.addAttribute("currentUsername", authentication.getName());
        model.addAttribute("roles", User.UserRole.values());

        return "user-detail";
    }

    /**
     * Change user password
     */
    @PostMapping("/{id}/change-password")
    public String changePassword(
            @PathVariable Long id,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {

        try {
            userService.changePassword(id, newPassword);
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ Đã đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "❌ Lỗi: " + e.getMessage());
        }

        return "redirect:/users/" + id;
    }
}
