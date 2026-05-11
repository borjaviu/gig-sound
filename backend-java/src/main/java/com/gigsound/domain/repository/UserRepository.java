package com.gigsound.domain.repository;

import com.gigsound.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UUID userId);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User update(User user);

    void delete(UUID userId);
}
