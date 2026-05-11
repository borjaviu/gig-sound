package com.gigsound.infrastructure.persistence.jpa;

import com.gigsound.infrastructure.persistence.model.EventJpaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventJpaRepository extends JpaRepository<EventJpaModel, UUID> {
}
