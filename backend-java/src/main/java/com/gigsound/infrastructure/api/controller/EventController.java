package com.gigsound.infrastructure.api.controller;

import com.gigsound.application.dto.EventCreateRequest;
import com.gigsound.application.dto.EventResponse;
import com.gigsound.application.dto.EventUpdateRequest;
import com.gigsound.domain.entity.Event;
import com.gigsound.domain.repository.EventRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * POST /events
     * Create a new event.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody EventCreateRequest request) {
        Event event = new Event(
                UUID.randomUUID(),
                request.getName(),
                request.getDate(),
                request.getVenue(),
                request.getTotalCapacity(),
                request.getTotalCapacity(), // available_tickets starts equal to total_capacity
                request.getTicketPrice(),
                LocalDateTime.now()
        );
        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }

    /**
     * GET /events
     * Get all events.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponse> listEvents() {
        return eventRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * GET /events/{eventId}
     * Get a specific event by ID.
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse getEvent(@PathVariable UUID eventId) {
        return eventRepository.findById(eventId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event " + eventId + " not found"));
    }

    /**
     * PUT /events/{eventId}
     * Update an existing event.
     */
    @PutMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponse updateEvent(@PathVariable UUID eventId,
                                     @Valid @RequestBody EventUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event " + eventId + " not found"));

        if (request.getName() != null) event.setName(request.getName());
        if (request.getDate() != null) event.setDate(request.getDate());
        if (request.getVenue() != null) event.setVenue(request.getVenue());
        if (request.getTotalCapacity() != null) event.setTotalCapacity(request.getTotalCapacity());
        if (request.getTicketPrice() != null) event.setTicketPrice(request.getTicketPrice());

        Event updated = eventRepository.update(event);
        return toResponse(updated);
    }

    /**
     * DELETE /events/{eventId}
     * Delete an event.
     */
    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable UUID eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event " + eventId + " not found"));
        eventRepository.delete(eventId);
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDate(),
                event.getVenue(),
                event.getTotalCapacity(),
                event.getAvailableTickets(),
                event.getTicketPrice(),
                event.getCreatedAt()
        );
    }
}
