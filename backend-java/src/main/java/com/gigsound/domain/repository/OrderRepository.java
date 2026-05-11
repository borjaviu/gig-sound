package com.gigsound.domain.repository;

import com.gigsound.domain.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);

    List<Order> findByEventId(UUID eventId);

    Order update(Order order);
}
