package com.devdiyora.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "books",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "isbn")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 100)
    private String authorName;


    @Column(nullable = false, length = 100)
    private String publisherName;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false)
    private Integer publicationYear;

    @Column(nullable = false, length = 30)
    private String edition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<BookCopy> copies = new ArrayList<>();

}