package com.hust.project1.controller;

import com.hust.project1.entity.Book;
import com.hust.project1.entity.BookCopy;
import com.hust.project1.service.BookCopyService;
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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/book-copies")
public class BookCopyController {

    @Autowired
    private BookCopyService bookCopyService;

    @Autowired
    private BookService bookService;

    /**
     * Display book copy list with search and pagination
     */
    @GetMapping
    public String listBookCopies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String copyNumber,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String location,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<BookCopy> bookCopyPage = bookCopyService.searchBookCopies(bookId, copyNumber, status, location, pageable);

        model.addAttribute("bookCopies", bookCopyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookCopyPage.getTotalPages());
        model.addAttribute("totalItems", bookCopyPage.getTotalElements());
        model.addAttribute("pageSize", size);

        // Keep search parameters
        model.addAttribute("searchBookId", bookId != null ? bookId : "");
        model.addAttribute("searchCopyNumber", copyNumber != null ? copyNumber : "");
        model.addAttribute("searchStatus", status != null ? status : "");
        model.addAttribute("searchLocation", location != null ? location : "");

        // Add status enum values for dropdown
        model.addAttribute("statuses", BookCopy.CopyStatus.values());

        return "book-copies";
    }

    /**
     * Show create book copy form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("bookCopy", new BookCopy());
        model.addAttribute("isEdit", false);

        // Get all books for dropdown
        List<Book> books = bookService.findAll(PageRequest.of(0, 1000)).getContent();
        model.addAttribute("books", books);
        model.addAttribute("statuses", BookCopy.CopyStatus.values());

        return "book-copy-form";
    }

    /**
     * Create new book copy
     */
    @PostMapping
    public String createBookCopy(@Valid @ModelAttribute BookCopy bookCopy,
            BindingResult result,
            @RequestParam Long bookId,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            List<Book> books = bookService.findAll(PageRequest.of(0, 1000)).getContent();
            model.addAttribute("books", books);
            model.addAttribute("statuses", BookCopy.CopyStatus.values());
            return "book-copy-form";
        }

        // Set the book
        Optional<Book> book = bookService.findById(bookId);
        if (book.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách!");
            return "redirect:/book-copies";
        }

        bookCopy.setBook(book.get());
        bookCopyService.save(bookCopy);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm bản sao mới thành công!");
        return "redirect:/book-copies";
    }

    /**
     * Show edit book copy form
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<BookCopy> bookCopy = bookCopyService.findById(id);

        if (bookCopy.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bản sao!");
            return "redirect:/book-copies";
        }

        model.addAttribute("bookCopy", bookCopy.get());
        model.addAttribute("isEdit", true);

        // Get all books for dropdown
        List<Book> books = bookService.findAll(PageRequest.of(0, 1000)).getContent();
        model.addAttribute("books", books);
        model.addAttribute("statuses", BookCopy.CopyStatus.values());

        return "book-copy-form";
    }

    /**
     * Update book copy
     */
    @PostMapping("/{id}")
    public String updateBookCopy(@PathVariable Long id,
            @Valid @ModelAttribute BookCopy bookCopy,
            @RequestParam Long bookId,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            List<Book> books = bookService.findAll(PageRequest.of(0, 1000)).getContent();
            model.addAttribute("books", books);
            model.addAttribute("statuses", BookCopy.CopyStatus.values());
            return "book-copy-form";
        }

        // Set the book
        Optional<Book> book = bookService.findById(bookId);
        if (book.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sách!");
            return "redirect:/book-copies";
        }

        bookCopy.setId(id);
        bookCopy.setBook(book.get());
        bookCopyService.save(bookCopy);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bản sao thành công!");
        return "redirect:/book-copies";
    }

    /**
     * Delete book copy
     */
    @PostMapping("/{id}/delete")
    public String deleteBookCopy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookCopyService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bản sao thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa bản sao. Bản sao có thể đang được mượn.");
        }
        return "redirect:/book-copies";
    }
}
