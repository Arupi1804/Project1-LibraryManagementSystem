package com.hust.project1.repository;

import com.hust.project1.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find book by ISBN (exact match)
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Search books by ISBN (partial match, case-insensitive) with pagination
     */
    Page<Book> findByIsbnContainingIgnoreCase(String isbn, Pageable pageable);

    /**
     * Search books by title (case-insensitive, partial match) with pagination
     */
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Search books by author (case-insensitive, partial match) with pagination
     */
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    /**
     * Find books by category with pagination
     */
    Page<Book> findByCategory(String category, Pageable pageable);

    /**
     * Check if ISBN exists
     */
    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    /**
     * Search books with multiple criteria
     */
    @Query("SELECT b FROM Book b WHERE " +
            "(:isbn IS NULL OR :isbn = '' OR b.isbn LIKE CONCAT('%', :isbn, '%')) AND " +
            "(:title IS NULL OR :title = '' OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:author IS NULL OR :author = '' OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:category IS NULL OR :category = '' OR LOWER(b.category) LIKE LOWER(CONCAT('%', :category, '%')))")
    Page<Book> searchCombined(@Param("isbn") String isbn,
            @Param("title") String title,
            @Param("author") String author,
            @Param("category") String category,
            Pageable pageable);

    /**
     * Find top borrowed books (for statistics)
     */
    @Query("SELECT b, COUNT(brd) as borrowCount FROM Book b " +
            "JOIN BookCopy bc ON bc.book = b " +
            "JOIN BorrowRecordDetail brd ON brd.bookCopy = bc " +
            "GROUP BY b ORDER BY borrowCount DESC")
    List<Object[]> findTopBorrowedBooks(Pageable pageable);

    /**
     * Find books that have never been borrowed
     */
    @Query("SELECT b FROM Book b WHERE b.id NOT IN " +
            "(SELECT DISTINCT bc.book.id FROM BookCopy bc " +
            "JOIN BorrowRecordDetail brd ON brd.bookCopy = bc)")
    List<Book> findNeverBorrowedBooks();
}
