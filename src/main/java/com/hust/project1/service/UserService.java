package com.hust.project1.service;

import com.hust.project1.entity.User;
import com.hust.project1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get all users with pagination
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Search users by keyword (username or email)
     */
    public Page<User> searchUsers(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(pageable);
        }
        return userRepository.searchByKeyword(keyword.trim(), pageable);
    }

    /**
     * Get user by ID
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Create new user
     */
    public User createUser(String username, String password, String email, User.UserRole role) {
        // Validate username uniqueness
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email đã tồn tại!");
        }

        // Validate password strength
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password phải có ít nhất 6 ký tự!");
        }

        // Create user
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(role);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    /**
     * Toggle user enabled/disabled status
     */
    public User toggleUserStatus(Long userId, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        // Prevent disabling own account
        if (user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Không thể khoá tài khoản của chính mình!");
        }

        user.setEnabled(!user.getEnabled());
        return userRepository.save(user);
    }

    /**
     * Change user role
     */
    public User changeUserRole(Long userId, User.UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long userId, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        // Prevent deleting own account
        if (user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Không thể xóa tài khoản của chính mình!");
        }

        userRepository.delete(user);
    }

    /**
     * Check if username exists
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Change user password (admin only)
     */
    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        // Validate password strength
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Password phải có ít nhất 6 ký tự!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}
