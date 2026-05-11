package com.gigsound.infrastructure.persistence.repository;

import com.gigsound.domain.entity.Event;
import com.gigsound.domain.repository.EventRepository;
import com.gigsound.infrastructure.persistence.jpa.EventJpaRepository;
import com.gigsound.infrastructure.persistence.model.EventJpaModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgresEventRepository implements EventRepository {

    private final EventJpaRepository jpaRepository;

    public PostgresEventRepository(EventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Event save(Event event) {
        EventJpaModel model = toModel(event);
        EventJpaModel saved = jpaRepository.save(model);
        return toDomain(saved);
    }

    @Override
    public Optional<Event> findById(UUID eventId) {
        return jpaRepository.findById(eventId).map(this::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Event update(Event event) {
        EventJpaModel model = jpaRepository.findById(event.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event " + event.getId() + " not found"));
        model.setName(event.getName());
        model.setDate(event.getDate());
        model.setVenue(event.getVenue());
        model.setTotalCapacity(event.getTotalCapacity());
        model.setAvailableTickets(event.getAvailableTickets());
        model.setTicketPrice(event.getTicketPrice());
        EventJpaModel updated = jpaRepository.save(model);
        return toDomain(updated);
    }

    @Override
    public void delete(UUID eventId) {
        jpaRepository.deleteById(eventId);
    }

    private EventJpaModel toModel(Event event) {
        EventJpaModel model = new EventJpaModel();
        model.setId(event.getId());
        model.setName(event.getName());
        model.setDate(event.getDate());
        model.setVenue(event.getVenue());
        model.setTotalCapacity(event.getTotalCapacity());
        model.setAvailableTickets(event.getAvailableTickets());
        model.setTicketPrice(event.getTicketPrice());
        model.setCreatedAt(event.getCreatedAt());
        return model;
    }

    private Event toDomain(EventJpaModel model) {
        return new Event(
                model.getId(),
                model.getName(),
                model.getDate(),
                model.getVenue(),
                model.getTotalCapacity(),
                model.getAvailableTickets(),
                model.getTicketPrice(),
                model.getCreatedAt()
        );
    }
}
