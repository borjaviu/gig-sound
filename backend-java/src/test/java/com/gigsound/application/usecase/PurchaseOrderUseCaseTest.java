package com.gigsound.application.usecase;

import com.gigsound.application.dto.OrderResponse;
import com.gigsound.application.dto.PurchaseOrderRequest;
import com.gigsound.domain.entity.Order;
import com.gigsound.domain.entity.OrderStatus;
import com.gigsound.domain.repository.OrderRepository;
import com.gigsound.domain.repository.UserRepository;
import com.gigsound.infrastructure.persistence.jpa.EventJpaRepository;
import com.gigsound.infrastructure.persistence.model.EventJpaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PurchaseOrderUseCase")
class PurchaseOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventJpaRepository eventJpaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PurchaseOrderUseCase purchaseOrderUseCase;

    private UUID userId;
    private UUID eventId;
    private EventJpaModel eventModel;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        eventModel = new EventJpaModel();
        eventModel.setId(eventId);
        eventModel.setName("Rock Festival");
        eventModel.setDate(LocalDateTime.now().plusDays(10));
        eventModel.setVenue("Main Stage");
        eventModel.setTotalCapacity(500);
        eventModel.setAvailableTickets(100);
        eventModel.setTicketPrice(50);
        eventModel.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("should create an order successfully when all conditions are met")
    void execute_success() {
        // Arrange
        PurchaseOrderRequest request = new PurchaseOrderRequest(eventId, userId, 2);

        com.gigsound.domain.entity.User domainUser = new com.gigsound.domain.entity.User(
                userId, "Alice", "alice@example.com", LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(domainUser));
        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.of(eventModel));

        Order savedOrder = new Order(
                UUID.randomUUID(), eventId, userId, 2, 100.0,
                OrderStatus.CONFIRMED, LocalDateTime.now(), null);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(eventJpaRepository.save(any(EventJpaModel.class))).thenReturn(eventModel);

        // Act
        OrderResponse response = purchaseOrderUseCase.execute(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getEventId()).isEqualTo(eventId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalPrice()).isEqualTo(100.0);
        assertThat(response.getStatus()).isEqualTo("confirmed");

        // Verify tickets were decremented
        ArgumentCaptor<EventJpaModel> eventCaptor = ArgumentCaptor.forClass(EventJpaModel.class);
        verify(eventJpaRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getAvailableTickets()).isEqualTo(98);
    }

    @Test
    @DisplayName("should throw 404 when user does not exist")
    void execute_userNotFound() {
        // Arrange
        PurchaseOrderRequest request = new PurchaseOrderRequest(eventId, userId, 2);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> purchaseOrderUseCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw 404 when event does not exist")
    void execute_eventNotFound() {
        // Arrange
        PurchaseOrderRequest request = new PurchaseOrderRequest(eventId, userId, 2);
        com.gigsound.domain.entity.User domainUser = new com.gigsound.domain.entity.User(
                userId, "Alice", "alice@example.com", LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(domainUser));
        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> purchaseOrderUseCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("should throw 400 when not enough tickets available")
    void execute_notEnoughTickets() {
        // Arrange
        PurchaseOrderRequest request = new PurchaseOrderRequest(eventId, userId, 200); // more than available
        com.gigsound.domain.entity.User domainUser = new com.gigsound.domain.entity.User(
                userId, "Alice", "alice@example.com", LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(domainUser));
        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.of(eventModel));

        // Act & Assert
        assertThatThrownBy(() -> purchaseOrderUseCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Not enough tickets");

        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("should calculate total price correctly")
    void execute_correctTotalPrice() {
        // Arrange: ticket_price=50, quantity=3 → total=150
        PurchaseOrderRequest request = new PurchaseOrderRequest(eventId, userId, 3);
        com.gigsound.domain.entity.User domainUser = new com.gigsound.domain.entity.User(
                userId, "Alice", "alice@example.com", LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(domainUser));
        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.of(eventModel));
        when(eventJpaRepository.save(any())).thenReturn(eventModel);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        Order savedOrder = new Order(UUID.randomUUID(), eventId, userId, 3, 150.0,
                OrderStatus.CONFIRMED, LocalDateTime.now(), null);
        when(orderRepository.save(orderCaptor.capture())).thenReturn(savedOrder);

        // Act
        purchaseOrderUseCase.execute(request);

        // Assert
        assertThat(orderCaptor.getValue().getTotalPrice()).isEqualTo(150.0);
    }
}
