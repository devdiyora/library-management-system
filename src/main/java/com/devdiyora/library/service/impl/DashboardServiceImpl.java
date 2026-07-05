package com.devdiyora.library.service.impl;

import com.devdiyora.library.dto.response.DashboardResponse;
import com.devdiyora.library.enums.BookCopyStatus;
import com.devdiyora.library.enums.BorrowStatus;
import com.devdiyora.library.enums.ReservationStatus;
import com.devdiyora.library.repository.BookCopyRepository;
import com.devdiyora.library.repository.BookRepository;
import com.devdiyora.library.repository.BorrowTransactionRepository;
import com.devdiyora.library.repository.ReservationRepository;
import com.devdiyora.library.repository.UserRepository;
import com.devdiyora.library.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final BookRepository bookRepository;

    private final BookCopyRepository bookCopyRepository;

    private final UserRepository userRepository;

    private final BorrowTransactionRepository borrowTransactionRepository;

    private final ReservationRepository reservationRepository;

    @Override
    public DashboardResponse getDashboard() {

        return new DashboardResponse(

                bookRepository.count(),

                bookCopyRepository.count(),

                bookCopyRepository.countByStatus(
                        BookCopyStatus.AVAILABLE
                ),

                bookCopyRepository.countByStatus(
                        BookCopyStatus.BORROWED
                ),

                userRepository.countMembers(),

                borrowTransactionRepository.countByStatus(
                        BorrowStatus.BORROWED
                ),

                reservationRepository.countByStatus(
                        ReservationStatus.PENDING
                )
        );
    }
}