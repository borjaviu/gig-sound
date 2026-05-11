package com.gigsound.application.usecase;

import com.gigsound.application.dto.OrderResponse;
import com.gigsound.application.dto.PurchaseOrderRequest;
import com.gigsound.domain.entity.Order;
import com.gigsound.domain.entity.OrderStatus;
import com.gigsound.domain.repository.EventRepository;
import com.gigsound.domain.repository.OrderRepository;
import com.gigsound.domain.repository.UserRepository;
import com.gigsound.infrastructure.persistence.jpa.EventJpaRepository;
import com.gigsound.infrastructure.persistence.model.EventJpaModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PurchaseOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventJpaRepository eventJpaRepository;
    private final UserRepository userRepository;

    public PurchaseOrderUseCase(OrderRepository orderRepository,
                                 EventJpaRepository eventJpaRepository,
                                 UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse execute(PurchaseOrderRequest request) {
        // Check if user exists
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User " + request.getUserId() + " not found"));

        // Check if event exists and has available tickets
        EventJpaModel event = eventJpaRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Event " + request.getEventId() + " not found"));

        if (event.getAvailableTickets() < request.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Not enough tickets available. Available: " + event.getAvailableTickets()
                            + ", Requested: " + request.getQuantity());
        }

        // Calculate total price
        double totalPrice = (double) request.getQuantity() * event.getTicketPrice();

        // Create order
        Order order = new Order(
                UUID.randomUUID(),
                request.getEventId(),
                request.getUserId(),
                request.getQuantity(),
                totalPrice,
                OrderStatus.CONFIRMED,
                LocalDateTime.now(),
                null
        );

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Update event available tickets
        event.setAvailableTickets(event.getAvailableTickets() - request.getQuantity());
        eventJpaRepository.save(event);

        return toResponse(savedOrder);
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
