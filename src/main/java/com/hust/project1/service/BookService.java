package com.hust.project1.service;

import com.hust.project1.entity.Book;
import com.hust.project1.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    
    /**
     * Get all books with pagination
     */
    public Page<Book> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    /**
     * Get book by ID
     */
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    /**
     * Create or update book
     */
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    /**
     * Delete book by ID
     */
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    /**
     * Search books with multiple criteria
     */
    public Page<Book> searchBooks(String title, String isbn, String author, String category, Pageable pageable) {
        return bookRepository.searchCombined(title, isbn, author, category, pageable);
    }

    /**
     * Check if ISBN already exists
     */
    public boolean existsByIsbn(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    /**
     * Check if ISBN exists for a different book (for edit validation)
     */
    public boolean existsByIsbnAndIdNot(String isbn, Long id) {
        return bookRepository.existsByIsbnAndIdNot(isbn, id);
    }

    // private boolean isEmpty(String str) {
    //     return str == null || str.trim().isEmpty();
    // }
}
