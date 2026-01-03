package com.hust.project1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_copies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "copy_number", nullable = false, length = 50)
    private String copyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CopyStatus status;

    @Column(length = 100)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @OneToMany(mappedBy = "bookCopy", cascade = CascadeType.ALL)
    private List<BorrowRecordDetail> borrowRecordDetails = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = CopyStatus.AVAILABLE;
        }
    }

    public enum CopyStatus {
        AVAILABLE,
        BORROWED,
        DAMAGED,
        LOST
    }
}
