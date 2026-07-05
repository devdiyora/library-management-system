package com.devdiyora.library.repository;

import com.devdiyora.library.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

     Optional<User> findByEmail(String email);

     @Query("""
    SELECT COUNT(u)
    FROM User u
    JOIN u.roles r
    WHERE r.name = 'MEMBER'
    """)
     long countMembers();

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("""
     SELECT u
     FROM User u
     WHERE u.id = :id
    """)
     Optional<User> findByIdForUpdate(@Param("id") Long id);
}