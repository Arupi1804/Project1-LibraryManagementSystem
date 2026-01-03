package com.hust.project1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_record_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal fine;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @PrePersist
    protected void onCreate() {
        if (fine == null) {
            fine = BigDecimal.ZERO;
        }
    }
}
