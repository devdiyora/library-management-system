package com.devdiyora.library.entity;

import com.devdiyora.library.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_transactions")
@Getter
@Setter
@NoArgsConstructor
public class BorrowTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private User issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_by")
    private User returnedBy;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status = BorrowStatus.BORROWED;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    public void issue(User member,
                      User librarian,
                      BookCopy bookCopy) {

        this.member = member;
        this.issuedBy = librarian;
        this.bookCopy = bookCopy;
        this.issueDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(14);
        this.status = BorrowStatus.BORROWED;
    }

    public void returnBook(User librarian) {

        this.returnedBy = librarian;
        this.returnDate = LocalDate.now();
        this.status = BorrowStatus.RETURNED;
    }
}