package com.hust.project1.controller;

import com.hust.project1.entity.Book;
import com.hust.project1.service.BookService;
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
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * Display book list with search and pagination
     */
    @GetMapping
    public String listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Book> bookPage = bookService.searchBooks(title, isbn, author, category, pageable);

        model.addAttribute("books", bookPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("totalItems", bookPage.getTotalElements());
        model.addAttribute("pageSize", size);

        // Keep search parameters
        model.addAttribute("searchTitle", title != null ? title : "");
        model.addAttribute("searchIsbn", isbn != null ? isbn : "");
        model.addAttribute("searchAuthor", author != null ? author : "");
        model.addAttribute("searchCategory", category != null ? category : "");

        return "books";
    }

    /**
     * Show create book form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("isEdit", false);
        return "book-form";
    }

    /**
     * Create new book
     */
    @PostMapping
    public String createBook(@Valid @ModelAttribute Book book,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Check ISBN uniqueness
        if (bookService.existsByIsbn(book.getIsbn())) {
            result.rejectValue("isbn", "error.book", "ISBN đã tồn tại trong hệ thống");
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "book-form";
        }

        bookService.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sách mới thành công!");
        return "redirect:/books";
    }

    /**
     * Show edit book form
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Book> book = bookService.findById(id);

        if (book.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách!");
            return "redirect:/books";
        }

        model.addAttribute("book", book.get());
        model.addAttribute("isEdit", true);
        return "book-form";
    }

    /**
     * Update book
     */
    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id,
            @Valid @ModelAttribute Book book,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Check ISBN uniqueness for other books
        if (bookService.existsByIsbnAndIdNot(book.getIsbn(), id)) {
            result.rejectValue("isbn", "error.book", "ISBN đã tồn tại trong hệ thống");
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "book-form";
        }

        book.setId(id);
        bookService.save(book);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sách thành công!");
        return "redirect:/books";
    }

    /**
     * Delete book
     */
    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sách thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa sách. Sách có thể đang được sử dụng.");
        }
        return "redirect:/books";
    }
}
