package com.devdiyora.library.repository;

import com.devdiyora.library.entity.BorrowTransaction;
import com.devdiyora.library.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowTransactionRepository
        extends JpaRepository<BorrowTransaction, Long> {

    Optional<BorrowTransaction> findByBookCopyIdAndStatus(
            Long bookCopyId,
            BorrowStatus status
    );

    List<BorrowTransaction> findByMemberId(Long memberId);

    List<BorrowTransaction> findByMemberIdAndStatus(
            Long memberId,
            BorrowStatus status
    );

    Optional<BorrowTransaction> findByBookCopyBarcodeAndStatus(
            String barcode,
            BorrowStatus status
    );

    List<BorrowTransaction> findAllByStatus(BorrowStatus status);

    long countByMemberIdAndStatus(
            Long memberId,
            BorrowStatus status
    );

    @Query("""
        SELECT bt
        FROM BorrowTransaction bt
        WHERE bt.member.id = :memberId
          AND bt.bookCopy.book.id = :bookId
          AND bt.status = :status
       """)
    Optional<BorrowTransaction> findActiveBorrowByMemberAndBook(
            @Param("memberId") Long memberId,
            @Param("bookId") Long bookId,
            @Param("status") BorrowStatus status
    );

    long countByStatus(BorrowStatus status);
}