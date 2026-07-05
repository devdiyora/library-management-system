package com.devdiyora.library.entity;

import com.devdiyora.library.enums.BookCopyStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "book_copies",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "barcode")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, unique = true, length = 50)
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCopyStatus status = BookCopyStatus.AVAILABLE;

    public boolean isAvailable() {
        return status == BookCopyStatus.AVAILABLE;
    }

    public void markAsBorrowed() {
        this.status = BookCopyStatus.BORROWED;
    }

    public void markAsAvailable() {
        this.status = BookCopyStatus.AVAILABLE;
    }
}