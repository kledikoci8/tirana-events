package com.tirana.events.repository;

import com.tirana.events.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findTop20ByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String fullName, String email);

    @Query("SELECT u FROM User u JOIN u.following f WHERE f.id = :userId")
    List<User> findUsersFollowing(@Param("userId") Long userId);
}
