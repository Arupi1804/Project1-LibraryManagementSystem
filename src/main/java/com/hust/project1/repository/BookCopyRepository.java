package com.hust.project1.repository;

import com.hust.project1.entity.BookCopy;
import com.hust.project1.entity.Book;
import com.hust.project1.entity.BookCopy.CopyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

        /**
         * Find all copies of a specific book
         */
        List<BookCopy> findByBook(Book book);

        /**
         * Find all copies of a specific book by book ID
         */
        List<BookCopy> findByBookId(Long bookId);

        /**
         * Find copies by status
         */
        List<BookCopy> findByStatus(CopyStatus status);

        /**
         * Find copies by book and status
         */
        List<BookCopy> findByBookAndStatus(Book book, CopyStatus status);

        /**
         * Find copies by book ID and status
         */
        List<BookCopy> findByBookIdAndStatus(Long bookId, CopyStatus status);

        /**
         * Count copies by book ID and status
         */
        Long countByBookIdAndStatus(Long bookId, CopyStatus status);

        /**
         * Combined search for book copies
         */
        @Query("SELECT bc FROM BookCopy bc JOIN bc.book b WHERE " +
                        "(:bookId IS NULL OR bc.book.id = :bookId) AND " +
                        "(:copyNumber IS NULL OR :copyNumber = '' OR bc.copyNumber LIKE CONCAT('%', :copyNumber, '%')) AND "
                        +
                        "(:status IS NULL OR :status = '' OR STR(bc.status) = :status) AND " +
                        "(:location IS NULL OR :location = '' OR LOWER(bc.location) LIKE LOWER(CONCAT('%', :location, '%')))")
        Page<BookCopy> searchCombined(@Param("bookId") Long bookId,
                        @Param("copyNumber") String copyNumber,
                        @Param("status") String status,
                        @Param("location") String location,
                        Pageable pageable);

        /**
         * Count book copies by status (for statistics)
         */
        long countByStatus(CopyStatus status);

        /**
         * Find book copies by multiple statuses (for damaged/lost reports)
         */
        List<BookCopy> findByStatusIn(List<CopyStatus> statuses);
}
