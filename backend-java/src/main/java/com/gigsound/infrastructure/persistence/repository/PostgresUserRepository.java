package com.gigsound.infrastructure.persistence.repository;

import com.gigsound.domain.entity.User;
import com.gigsound.domain.repository.UserRepository;
import com.gigsound.infrastructure.persistence.jpa.UserJpaRepository;
import com.gigsound.infrastructure.persistence.model.UserJpaModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgresUserRepository implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public PostgresUserRepository(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaModel model = toModel(user);
        UserJpaModel saved = jpaRepository.save(model);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return jpaRepository.findById(userId).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public User update(User user) {
        UserJpaModel model = jpaRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User " + user.getId() + " not found"));
        model.setName(user.getName());
        model.setEmail(user.getEmail());
        UserJpaModel updated = jpaRepository.save(model);
        return toDomain(updated);
    }

    @Override
    public void delete(UUID userId) {
        jpaRepository.deleteById(userId);
    }

    private UserJpaModel toModel(User user) {
        UserJpaModel model = new UserJpaModel();
        model.setId(user.getId());
        model.setName(user.getName());
        model.setEmail(user.getEmail());
        model.setCreatedAt(user.getCreatedAt());
        return model;
    }

    private User toDomain(UserJpaModel model) {
        return new User(
                model.getId(),
                model.getName(),
                model.getEmail(),
                model.getCreatedAt()
        );
    }
}
