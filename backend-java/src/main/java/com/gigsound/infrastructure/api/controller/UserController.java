package com.gigsound.infrastructure.api.controller;

import com.gigsound.application.dto.UserCreateRequest;
import com.gigsound.application.dto.UserResponse;
import com.gigsound.application.dto.UserUpdateRequest;
import com.gigsound.domain.entity.User;
import com.gigsound.domain.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * POST /users
     * Create a new user.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        // Check if email already exists
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User with email " + request.getEmail() + " already exists");
        });

        User user = new User(
                UUID.randomUUID(),
                request.getName(),
                request.getEmail(),
                LocalDateTime.now()
        );
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    /**
     * GET /users
     * Get all users.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * GET /users/{userId}
     * Get a specific user by ID.
     */
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable UUID userId) {
        return userRepository.findById(userId)
                .map(this::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User " + userId + " not found"));
    }

    /**
     * PUT /users/{userId}
     * Update an existing user.
     */
    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(@PathVariable UUID userId,
                                   @Valid @RequestBody UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User " + userId + " not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) {
            // Check if email is already taken by another user
            userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(userId)) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Email " + request.getEmail() + " is already taken");
                }
            });
            user.setEmail(request.getEmail());
        }

        User updated = userRepository.update(user);
        return toResponse(updated);
    }

    /**
     * DELETE /users/{userId}
     * Delete a user.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User " + userId + " not found"));
        userRepository.delete(userId);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}
