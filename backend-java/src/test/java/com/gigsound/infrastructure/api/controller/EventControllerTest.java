package com.gigsound.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigsound.application.dto.EventCreateRequest;
import com.gigsound.application.dto.EventUpdateRequest;
import com.gigsound.domain.entity.Event;
import com.gigsound.domain.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@ActiveProfiles("test")
@DisplayName("EventController")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventRepository eventRepository;

    private Event sampleEvent(UUID id) {
        return new Event(
                id, "Rock Festival", LocalDateTime.now().plusDays(10),
                "Main Arena", 500, 500, 50, LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /events - should return 201 when event is created")
    void createEvent_success() throws Exception {
        UUID eventId = UUID.randomUUID();
        EventCreateRequest request = new EventCreateRequest(
                "Rock Festival", LocalDateTime.now().plusDays(10), "Main Arena", 500, 50);

        when(eventRepository.save(any(Event.class))).thenReturn(sampleEvent(eventId));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rock Festival"))
                .andExpect(jsonPath("$.totalCapacity").value(500))
                .andExpect(jsonPath("$.ticketPrice").value(50));
    }

    @Test
    @DisplayName("POST /events - should return 422 when name is missing")
    void createEvent_missingName() throws Exception {
        String body = """
                {
                  "date": "2026-12-01T20:00:00",
                  "venue": "Arena",
                  "totalCapacity": 100,
                  "ticketPrice": 30
                }
                """;

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /events - should return 200 with list of events")
    void listEvents_success() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        when(eventRepository.findAll()).thenReturn(List.of(sampleEvent(id1), sampleEvent(id2)));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /events/{eventId} - should return 200 when event exists")
    void getEvent_success() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent(eventId)));

        mockMvc.perform(get("/events/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.toString()));
    }

    @Test
    @DisplayName("GET /events/{eventId} - should return 404 when event not found")
    void getEvent_notFound() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/events/{eventId}", eventId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /events/{eventId} - should return 200 when updated successfully")
    void updateEvent_success() throws Exception {
        UUID eventId = UUID.randomUUID();
        Event existing = sampleEvent(eventId);
        Event updated = new Event(eventId, "Updated Festival", existing.getDate(),
                "New Venue", 600, 600, 75, existing.getCreatedAt());

        EventUpdateRequest request = new EventUpdateRequest();
        request.setName("Updated Festival");
        request.setVenue("New Venue");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existing));
        when(eventRepository.update(any(Event.class))).thenReturn(updated);

        mockMvc.perform(put("/events/{eventId}", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Festival"));
    }

    @Test
    @DisplayName("DELETE /events/{eventId} - should return 204 when deleted")
    void deleteEvent_success() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent(eventId)));
        doNothing().when(eventRepository).delete(eventId);

        mockMvc.perform(delete("/events/{eventId}", eventId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /events/{eventId} - should return 404 when event not found")
    void deleteEvent_notFound() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/events/{eventId}", eventId))
                .andExpect(status().isNotFound());
    }
}
