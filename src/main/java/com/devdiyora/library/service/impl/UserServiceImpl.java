package com.devdiyora.library.service.impl;

import com.devdiyora.library.dto.request.LoginRequest;
import com.devdiyora.library.dto.request.RegisterRequest;
import com.devdiyora.library.dto.response.*;
import com.devdiyora.library.entity.Role;
import com.devdiyora.library.entity.User;
import com.devdiyora.library.enums.RoleType;
import com.devdiyora.library.exception.DuplicateResourceException;
import com.devdiyora.library.exception.ResourceNotFoundException;
import com.devdiyora.library.repository.RoleRepository;
import com.devdiyora.library.repository.UserRepository;
import com.devdiyora.library.security.CustomUserDetails;
import com.devdiyora.library.security.jwt.JwtService;
import com.devdiyora.library.service.UserService;
import com.devdiyora.library.util.CurrentUserProvider;
import com.devdiyora.library.util.SortFieldValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final List<String> ALLOWED_SORT_FIELDS = List.of(
            "firstName",
            "lastName",
            "email"
    );

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CurrentUserProvider currentUserProvider;

    private RegisterResponse createUser(RegisterRequest request, RoleType roleType) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email already exists.");
        }

        Role role = roleRepository.findByName(roleType.name())
                .orElseThrow(() ->
                        new ResourceNotFoundException(roleType.name() + " role not found."));

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEnabled(true);

        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                role.getName()
        );
    }


    @Override
    public RegisterResponse register(RegisterRequest request) {
        return createUser(request, RoleType.MEMBER);
    }

    @Override
    public RegisterResponse createLibrarian(RegisterRequest request) {

        return createUser(request, RoleType.LIBRARIAN);
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        String token = jwtService.generateToken(
                new CustomUserDetails(user)
        );

        return new LoginResponse(
                token,
                "Bearer"
        );
    }

    @Override
    public CurrentUserResponse getCurrentUser() {

        User user = currentUserProvider.getCurrentUser();

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return new CurrentUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                roles
        );
    }

    private UserResponse mapToUserResponse(User user) {

        String role = user.getRoles()
                .stream()
                .findFirst()
                .map(Role::getName)
                .orElse("");

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getEnabled(),
                role
        );
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        SortFieldValidator.validate(sortBy, ALLOWED_SORT_FIELDS);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> responses = userPage.getContent()
                .stream()
                .map(this::mapToUserResponse)
                .toList();

        return new PageResponse<>(
                responses,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Override
    public UserResponse toggleUserStatus(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        user.setEnabled(!user.getEnabled());

        User updatedUser = userRepository.save(user);

        return mapToUserResponse(updatedUser);
    }
}