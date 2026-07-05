package com.devdiyora.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // This field will store the BCrypt hashed password,
    // never the plain text password.
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    // If false, the user cannot access the system.
    @Column(nullable = false)
    private Boolean enabled = true;

    // A user can have multiple roles (e.g. ADMIN + LIBRARIAN).
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    public boolean hasRole(String roleName) {

        return roles.stream()
                .map(Role::getName)
                .anyMatch(role -> role.equals(roleName));
    }
}