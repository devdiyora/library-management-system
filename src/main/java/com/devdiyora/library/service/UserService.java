package com.devdiyora.library.service;

import com.devdiyora.library.dto.request.LoginRequest;
import com.devdiyora.library.dto.request.RegisterRequest;
import com.devdiyora.library.dto.response.*;

public interface UserService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RegisterResponse createLibrarian(RegisterRequest request);

    CurrentUserResponse getCurrentUser();

    PageResponse<UserResponse> getAllUsers(
            int page,
            int size,
            String sortBy,
            String direction
    );

    UserResponse toggleUserStatus(Long id);
}