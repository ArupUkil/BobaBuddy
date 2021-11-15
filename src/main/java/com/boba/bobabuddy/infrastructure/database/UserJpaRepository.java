package com.boba.bobabuddy.infrastructure.database;

import com.boba.bobabuddy.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<User, String> {
    List<User> findByName(String name);

    Optional<User> removeByEmail(String email);

    Optional<User> findByRatings_id(UUID id);
}