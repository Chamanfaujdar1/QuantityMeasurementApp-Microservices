package com.chaman.auth.repository;

import com.chaman.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :loginId OR u.email = :loginId")
    Optional<User> findByUsernameOrEmail(@Param("loginId") String loginId);
}
