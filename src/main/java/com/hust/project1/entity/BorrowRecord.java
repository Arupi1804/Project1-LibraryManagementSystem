package com.hust.project1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrow_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecordStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "borrowRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowRecordDetail> borrowRecordDetails = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (borrowDate == null) {
            borrowDate = LocalDate.now();
        }
        if (dueDate == null) {
            dueDate = borrowDate.plusDays(14); // Default 14 days loan period
        }
        if (status == null) {
            status = RecordStatus.ACTIVE;
        }
    }

    // Helper method to add borrow record detail
    public void addBorrowRecordDetail(BorrowRecordDetail detail) {
        borrowRecordDetails.add(detail);
        detail.setBorrowRecord(this);
    }

    // Helper method to remove borrow record detail
    public void removeBorrowRecordDetail(BorrowRecordDetail detail) {
        borrowRecordDetails.remove(detail);
        detail.setBorrowRecord(null);
    }

    public enum RecordStatus {
        ACTIVE,
        RETURNED,
        OVERDUE
    }
}
