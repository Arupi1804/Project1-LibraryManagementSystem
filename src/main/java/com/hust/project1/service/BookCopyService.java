package com.hust.project1.service;

import com.hust.project1.entity.BookCopy;
import com.hust.project1.repository.BookCopyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class BookCopyService {

    @Autowired
    private BookCopyRepository bookCopyRepository;

    /**
     * Get all book copies with pagination
     */
    public Page<BookCopy> findAll(Pageable pageable) {
        return bookCopyRepository.findAll(pageable);
    }

    /**
     * Get book copy by ID
     */
    public Optional<BookCopy> findById(Long id) {
        return bookCopyRepository.findById(id);
    }

    /**
     * Create or update book copy
     */
    public BookCopy save(BookCopy bookCopy) {
        return bookCopyRepository.save(bookCopy);
    }

    /**
     * Delete book copy by ID
     */
    public void deleteById(Long id) {
        bookCopyRepository.deleteById(id);
    }

    /**
     * Search book copies with multiple criteria
     */
    public Page<BookCopy> searchBookCopies(Long bookId, String copyNumber, String status,
            String location, Pageable pageable) {
        return bookCopyRepository.searchCombined(bookId, copyNumber, status, location, pageable);
    }

    /**
     * Count copies by book ID and status
     */
    public Long countByBookIdAndStatus(Long bookId, BookCopy.CopyStatus status) {
        return bookCopyRepository.countByBookIdAndStatus(bookId, status);
    }
}
