package com.gigsound.application.usecase;

import com.gigsound.application.dto.CancelOrderRequest;
import com.gigsound.application.dto.OrderResponse;
import com.gigsound.domain.entity.Order;
import com.gigsound.domain.entity.OrderStatus;
import com.gigsound.domain.repository.OrderRepository;
import com.gigsound.infrastructure.persistence.jpa.EventJpaRepository;
import com.gigsound.infrastructure.persistence.model.EventJpaModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventJpaRepository eventJpaRepository;

    public CancelOrderUseCase(OrderRepository orderRepository,
                               EventJpaRepository eventJpaRepository) {
        this.orderRepository = orderRepository;
        this.eventJpaRepository = eventJpaRepository;
    }

    @Transactional
    public OrderResponse execute(CancelOrderRequest request) {
        // Find order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order " + request.getOrderId() + " not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Order " + request.getOrderId() + " is already cancelled");
        }

        // Update order status
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        Order updatedOrder = orderRepository.update(order);

        // Restore tickets to event
        eventJpaRepository.findById(order.getEventId()).ifPresent(event -> {
            event.setAvailableTickets(event.getAvailableTickets() + order.getQuantity());
            eventJpaRepository.save(event);
        });

        return toResponse(updatedOrder);
    }

    private OrderResponse toResponse(Order order) {
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
