package com.gigsound.infrastructure.persistence.repository;

import com.gigsound.domain.entity.Order;
import com.gigsound.domain.entity.OrderStatus;
import com.gigsound.domain.repository.OrderRepository;
import com.gigsound.infrastructure.persistence.jpa.OrderJpaRepository;
import com.gigsound.infrastructure.persistence.model.OrderJpaModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgresOrderRepository implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    public PostgresOrderRepository(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderJpaModel model = toModel(order);
        OrderJpaModel saved = jpaRepository.save(model);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findById(orderId).map(this::toDomain);
    }

    @Override
    public List<Order> findByEventId(UUID eventId) {
        return jpaRepository.findByEventId(eventId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Order update(Order order) {
        OrderJpaModel model = jpaRepository.findById(order.getId())
                .orElseThrow(() -> new IllegalArgumentException("Order " + order.getId() + " not found"));
        model.setStatus(order.getStatus().getValue());
        model.setCancelledAt(order.getCancelledAt());
        OrderJpaModel updated = jpaRepository.save(model);
        return toDomain(updated);
    }

    private OrderJpaModel toModel(Order order) {
        OrderJpaModel model = new OrderJpaModel();
        model.setId(order.getId());
        model.setEventId(order.getEventId());
        model.setUserId(order.getUserId());
        model.setQuantity(order.getQuantity());
        model.setTotalPrice(order.getTotalPrice());
        model.setStatus(order.getStatus().getValue());
        model.setCreatedAt(order.getCreatedAt());
        model.setCancelledAt(order.getCancelledAt());
        return model;
    }

    private Order toDomain(OrderJpaModel model) {
        return new Order(
                model.getId(),
                model.getEventId(),
                model.getUserId(),
                model.getQuantity(),
                model.getTotalPrice(),
                OrderStatus.fromValue(model.getStatus()),
                model.getCreatedAt(),
                model.getCancelledAt()
        );
    }
}
