package com.gigsound.application.dto;

import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public class EventUpdateRequest {

    private String name;
    private LocalDateTime date;
    private String venue;

    @Min(value = 1, message = "total_capacity must be greater than 0")
    private Integer totalCapacity;

    @Min(value = 1, message = "ticket_price must be greater than 0")
    private Integer ticketPrice;

    public EventUpdateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public Integer getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Integer totalCapacity) { this.totalCapacity = totalCapacity; }

    public Integer getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(Integer ticketPrice) { this.ticketPrice = ticketPrice; }
}
