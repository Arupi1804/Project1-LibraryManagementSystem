package com.hust.project1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String author;

    @Column(length = 100)
    private String publisher;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Column(length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCopy> bookCopies = new ArrayList<>();

    // Helper method to add book copy
    public void addBookCopy(BookCopy bookCopy) {
        bookCopies.add(bookCopy);
        bookCopy.setBook(this);
    }

    // Helper method to remove book copy
    public void removeBookCopy(BookCopy bookCopy) {
        bookCopies.remove(bookCopy);
        bookCopy.setBook(null);
    }
}
