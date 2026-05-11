package com.gigsound.infrastructure.persistence.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
public class EventJpaModel {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false, length = 255)
    private String venue;

    @Column(name = "total_capacity", nullable = false)
    private int totalCapacity;

    @Column(name = "available_tickets", nullable = false)
    private int availableTickets;

    @Column(name = "ticket_price", nullable = false)
    private int ticketPrice = 50;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<OrderJpaModel> orders;

    public EventJpaModel() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getAvailableTickets() { return availableTickets; }
    public void setAvailableTickets(int availableTickets) { this.availableTickets = availableTickets; }

    public int getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(int ticketPrice) { this.ticketPrice = ticketPrice; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderJpaModel> getOrders() { return orders; }
    public void setOrders(List<OrderJpaModel> orders) { this.orders = orders; }
}
