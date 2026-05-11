package com.gigsound.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigsound.application.dto.UserCreateRequest;
import com.gigsound.application.dto.UserUpdateRequest;
import com.gigsound.domain.entity.User;
import com.gigsound.domain.repository.UserRepository;
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

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    private User sampleUser(UUID id) {
        return new User(id, "Alice", "alice@example.com", LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /users - should return 201 when user is created")
    void createUser_success() throws Exception {
        UUID userId = UUID.randomUUID();
        UserCreateRequest request = new UserCreateRequest("Alice", "alice@example.com");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(sampleUser(userId));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("POST /users - should return 400 when email already exists")
    void createUser_duplicateEmail() throws Exception {
        UUID userId = UUID.randomUUID();
        UserCreateRequest request = new UserCreateRequest("Alice", "alice@example.com");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(sampleUser(userId)));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /users - should return 422 when email is invalid")
    void createUser_invalidEmail() throws Exception {
        String body = """
                {"name": "Bob", "email": "not-an-email"}
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /users - should return 200 with list of users")
    void listUsers_success() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(
                sampleUser(UUID.randomUUID()),
                sampleUser(UUID.randomUUID())));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /users/{userId} - should return 200 when user exists")
    void getUser_success() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser(userId)));

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));
    }

    @Test
    @DisplayName("GET /users/{userId} - should return 404 when user not found")
    void getUser_notFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /users/{userId} - should return 200 when updated successfully")
    void updateUser_success() throws Exception {
        UUID userId = UUID.randomUUID();
        User existing = sampleUser(userId);
        User updated = new User(userId, "Alice Updated", "alice@example.com", existing.getCreatedAt());

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Alice Updated");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.update(any(User.class))).thenReturn(updated);

        mockMvc.perform(put("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Updated"));
    }

    @Test
    @DisplayName("DELETE /users/{userId} - should return 204 when deleted")
    void deleteUser_success() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser(userId)));
        doNothing().when(userRepository).delete(userId);

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /users/{userId} - should return 404 when user not found")
    void deleteUser_notFound() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }
}
