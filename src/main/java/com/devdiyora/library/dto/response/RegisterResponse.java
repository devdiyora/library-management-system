package com.devdiyora.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

}