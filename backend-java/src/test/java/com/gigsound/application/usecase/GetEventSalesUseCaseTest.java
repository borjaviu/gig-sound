package com.gigsound.application.usecase;

import com.gigsound.application.dto.EventSalesResponse;
import com.gigsound.domain.entity.Order;
import com.gigsound.domain.entity.OrderStatus;
import com.gigsound.domain.repository.OrderRepository;
import com.gigsound.infrastructure.persistence.jpa.EventJpaRepository;
import com.gigsound.infrastructure.persistence.model.EventJpaModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetEventSalesUseCase")
class GetEventSalesUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventJpaRepository eventJpaRepository;

    @InjectMocks
    private GetEventSalesUseCase getEventSalesUseCase;

    private UUID eventId;
    private EventJpaModel eventModel;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();

        eventModel = new EventJpaModel();
        eventModel.setId(eventId);
        eventModel.setName("Jazz Night");
        eventModel.setDate(LocalDateTime.now().plusDays(5));
        eventModel.setVenue("Blue Note");
        eventModel.setTotalCapacity(200);
        eventModel.setAvailableTickets(150);
        eventModel.setTicketPrice(80);
        eventModel.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("should return event sales with correct totals for confirmed orders")
    void execute_success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Order confirmed1 = new Order(UUID.randomUUID(), eventId, userId, 3, 240.0,
                OrderStatus.CONFIRMED, LocalDateTime.now(), null);
        Order confirmed2 = new Order(UUID.randomUUID(), eventId, userId, 2, 160.0,
                OrderStatus.CONFIRMED, LocalDateTime.now(), null);
        Order cancelled = new Order(UUID.randomUUID(), eventId, userId, 1, 80.0,
                OrderStatus.CANCELLED, LocalDateTime.now(), LocalDateTime.now());

        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.of(eventModel));
        when(orderRepository.findByEventId(eventId)).thenReturn(List.of(confirmed1, confirmed2, cancelled));

        // Act
        EventSalesResponse response = getEventSalesUseCase.execute(eventId);

        // Assert
        assertThat(response.getEventId()).isEqualTo(eventId);
        assertThat(response.getEventName()).isEqualTo("Jazz Night");
        assertThat(response.getTotalSales()).isEqualTo(5); // 3 + 2 confirmed tickets
        assertThat(response.getTotalRevenue()).isEqualTo(400.0); // 240 + 160
        assertThat(response.getOrders()).hasSize(3); // all orders returned
    }

    @Test
    @DisplayName("should return zero totals when no confirmed orders exist")
    void execute_noConfirmedOrders() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Order cancelled = new Order(UUID.randomUUID(), eventId, userId, 2, 160.0,
                OrderStatus.CANCELLED, LocalDateTime.now(), LocalDateTime.now());

        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.of(eventModel));
        when(orderRepository.findByEventId(eventId)).thenReturn(List.of(cancelled));

        // Act
        EventSalesResponse response = getEventSalesUseCase.execute(eventId);

        // Assert
        assertThat(response.getTotalSales()).isEqualTo(0);
        assertThat(response.getTotalRevenue()).isEqualTo(0.0);
        assertThat(response.getOrders()).hasSize(1);
    }

    @Test
    @DisplayName("should return empty sales when no orders exist for event")
    void execute_noOrders() {
        // Arrange
        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.of(eventModel));
        when(orderRepository.findByEventId(eventId)).thenReturn(List.of());

        // Act
        EventSalesResponse response = getEventSalesUseCase.execute(eventId);

        // Assert
        assertThat(response.getTotalSales()).isEqualTo(0);
        assertThat(response.getTotalRevenue()).isEqualTo(0.0);
        assertThat(response.getOrders()).isEmpty();
    }

    @Test
    @DisplayName("should throw 404 when event does not exist")
    void execute_eventNotFound() {
        // Arrange
        when(eventJpaRepository.findById(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> getEventSalesUseCase.execute(eventId))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not found");
    }
}
