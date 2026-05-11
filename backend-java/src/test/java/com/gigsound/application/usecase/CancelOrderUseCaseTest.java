package com.gigsound.application.usecase;

import com.gigsound.application.dto.CancelOrderRequest;
import com.gigsound.application.dto.OrderResponse;
import com.gigsound.domain.entity.Order;
import com.gigsound.domain.entity.OrderStatus;
import com.gigsound.domain.repository.OrderRepository;
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
@DisplayName("CancelOrderUseCase")
class CancelOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventJpaRepository eventJpaRepository;

    @InjectMocks
    private CancelOrderUseCase cancelOrderUseCase;

    private UUID orderId;
    private UUID eventId;
    private Order confirmedOrder;
    private EventJpaModel eventModel;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        confirmedOrder = new Order(
                orderId, eventId, UUID.randomUUID(), 2, 100.0,
                OrderStatus.CONFIRMED, LocalDateTime.now(), null);

        eventModel = new EventJpaModel();
        eventModel.setId(eventId);
        eventModel.setName("Rock Festival");
        eventModel.setAvailableTickets(50);
        eventModel.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("should cancel an order successfully")
    void execute_success() {
        // Arrange
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(confirmedOrder));

        Order cancelledOrder = new Order(
                orderId, eventId, confirmedOrder.getUserId(), 2, 100.0,
                OrderStatus.CANCELLED, confirmedOrder.getCreatedAt(), LocalDateTime.now());
        when(orderRepository.update(any(Order.class))).thenReturn(cancelledOrder);
        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.of(eventModel));
        when(eventJpaRepository.save(any())).thenReturn(eventModel);

        // Act
        OrderResponse response = cancelOrderUseCase.execute(request);

        // Assert
        assertThat(response.getStatus()).isEqualTo("cancelled");
        assertThat(response.getCancelledAt()).isNotNull();
    }

    @Test
    @DisplayName("should restore available tickets after cancellation")
    void execute_restoresTickets() {
        // Arrange
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(confirmedOrder));
        when(orderRepository.update(any())).thenReturn(confirmedOrder);
        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.of(eventModel));

        ArgumentCaptor<EventJpaModel> eventCaptor = ArgumentCaptor.forClass(EventJpaModel.class);
        when(eventJpaRepository.save(eventCaptor.capture())).thenReturn(eventModel);

        // Act
        cancelOrderUseCase.execute(request);

        // Assert: tickets restored (50 + 2 = 52)
        assertThat(eventCaptor.getValue().getAvailableTickets()).isEqualTo(52);
    }

    @Test
    @DisplayName("should throw 404 when order does not exist")
    void execute_orderNotFound() {
        // Arrange
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cancelOrderUseCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");

        verify(orderRepository, never()).update(any());
    }

    @Test
    @DisplayName("should throw 400 when order is already cancelled")
    void execute_alreadyCancelled() {
        // Arrange
        Order alreadyCancelled = new Order(
                orderId, eventId, UUID.randomUUID(), 2, 100.0,
                OrderStatus.CANCELLED, LocalDateTime.now(), LocalDateTime.now());
        CancelOrderRequest request = new CancelOrderRequest(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(alreadyCancelled));

        // Act & Assert
        assertThatThrownBy(() -> cancelOrderUseCase.execute(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already cancelled");

        verify(orderRepository, never()).update(any());
    }
}
