package com.gigsound.infrastructure.persistence.jpa;

import com.gigsound.infrastructure.persistence.model.UserJpaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaModel, UUID> {

    Optional<UserJpaModel> findByEmail(String email);
}
