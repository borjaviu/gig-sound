package com.gigsound.application.usecase;

import com.gigsound.application.dto.EventSalesResponse;
import com.gigsound.application.dto.OrderResponse;
import com.gigsound.domain.entity.Order;
import com.gigsound.domain.entity.OrderStatus;
import com.gigsound.domain.repository.OrderRepository;
import com.gigsound.infrastructure.persistence.jpa.EventJpaRepository;
import com.gigsound.infrastructure.persistence.model.EventJpaModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GetEventSalesUseCase {

    private final OrderRepository orderRepository;
    private final EventJpaRepository eventJpaRepository;

    public GetEventSalesUseCase(OrderRepository orderRepository,
                                 EventJpaRepository eventJpaRepository) {
        this.orderRepository = orderRepository;
        this.eventJpaRepository = eventJpaRepository;
    }

    public EventSalesResponse execute(UUID eventId) {
        // Check if event exists
        EventJpaModel event = eventJpaRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event " + eventId + " not found"));

        // Get all orders for this event
        List<Order> orders = orderRepository.findByEventId(eventId);

        // Filter only confirmed orders for sales calculation
        List<Order> confirmedOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.CONFIRMED)
                .collect(Collectors.toList());

        // Calculate totals
        int totalSales = confirmedOrders.stream().mapToInt(Order::getQuantity).sum();
        double totalRevenue = confirmedOrders.stream().mapToDouble(Order::getTotalPrice).sum();

        // Convert orders to response DTOs
        List<OrderResponse> orderResponses = orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());

        return new EventSalesResponse(
                eventId,
                event.getName(),
                totalSales,
                totalRevenue,
                orderResponses
        );
    }

    private OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getEventId(),
                order.getUserId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus().getValue(),
                order.getCreatedAt(),
                order.getCancelledAt()
        );
    }
}
