package com.gigsound.domain.repository;

import com.gigsound.domain.entity.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository {

    Event save(Event event);

    Optional<Event> findById(UUID eventId);

    List<Event> findAll();

    Event update(Event event);

    void delete(UUID eventId);
}
