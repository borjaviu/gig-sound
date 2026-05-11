package com.gigsound.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigsound.application.dto.CancelOrderRequest;
import com.gigsound.application.dto.EventSalesResponse;
import com.gigsound.application.dto.OrderResponse;
import com.gigsound.application.dto.PurchaseOrderRequest;
import com.gigsound.application.usecase.CancelOrderUseCase;
import com.gigsound.application.usecase.GetEventSalesUseCase;
import com.gigsound.application.usecase.PurchaseOrderUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
@DisplayName("OrderController")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PurchaseOrderUseCase purchaseOrderUseCase;

    @MockBean
    private CancelOrderUseCase cancelOrderUseCase;

    @MockBean
    private GetEventSalesUseCase getEventSalesUseCase;

    @Test
    @DisplayName("POST /orders/purchase - should return 201 when purchase is successful")
    void purchaseOrder_success() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        PurchaseOrderRequest request = new PurchaseOrderRequest(eventId, userId, 2);
        OrderResponse response = new OrderResponse(
                orderId, eventId, userId, 2, 100.0,
                "confirmed", LocalDateTime.now(), null);

        when(purchaseOrderUseCase.execute(any(PurchaseOrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/orders/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("confirmed"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(100.0));
    }

    @Test
    @DisplayName("POST /orders/purchase - should return 404 when event not found")
    void purchaseOrder_eventNotFound() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        PurchaseOrderRequest request = new PurchaseOrderRequest(eventId, userId, 2);
        when(purchaseOrderUseCase.execute(any()))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Event " + eventId + " not found"));

        mockMvc.perform(post("/orders/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /orders/purchase - should return 400 when not enough tickets")
    void purchaseOrder_notEnoughTickets() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        PurchaseOrderRequest request = new PurchaseOrderRequest(eventId, userId, 9999);
        when(purchaseOrderUseCase.execute(any()))
                .thenThrow(new ResponseStatusException(BAD_REQUEST, "Not enough tickets available"));

        mockMvc.perform(post("/orders/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /orders/cancel - should return 200 when cancel is successful")
    void cancelOrder_success() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CancelOrderRequest request = new CancelOrderRequest(orderId);
        OrderResponse response = new OrderResponse(
                orderId, eventId, userId, 2, 100.0,
                "cancelled", LocalDateTime.now(), LocalDateTime.now());

        when(cancelOrderUseCase.execute(any(CancelOrderRequest.class))).thenReturn(response);

        mockMvc.perform(delete("/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("cancelled"));
    }

    @Test
    @DisplayName("DELETE /orders/cancel - should return 400 when already cancelled")
    void cancelOrder_alreadyCancelled() throws Exception {
        UUID orderId = UUID.randomUUID();

        CancelOrderRequest request = new CancelOrderRequest(orderId);
        when(cancelOrderUseCase.execute(any()))
                .thenThrow(new ResponseStatusException(BAD_REQUEST, "Order " + orderId + " is already cancelled"));

        mockMvc.perform(delete("/orders/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /orders/event/{eventId}/sales - should return 200 with sales data")
    void getEventSales_success() throws Exception {
        UUID eventId = UUID.randomUUID();

        EventSalesResponse response = new EventSalesResponse(
                eventId, "Rock Festival", 5, 250.0, List.of());

        when(getEventSalesUseCase.execute(eventId)).thenReturn(response);

        mockMvc.perform(get("/orders/event/{eventId}/sales", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName").value("Rock Festival"))
                .andExpect(jsonPath("$.totalSales").value(5))
                .andExpect(jsonPath("$.totalRevenue").value(250.0));
    }

    @Test
    @DisplayName("GET /orders/event/{eventId}/sales - should return 404 when event not found")
    void getEventSales_eventNotFound() throws Exception {
        UUID eventId = UUID.randomUUID();

        when(getEventSalesUseCase.execute(eventId))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Event " + eventId + " not found"));

        mockMvc.perform(get("/orders/event/{eventId}/sales", eventId))
                .andExpect(status().isNotFound());
    }
}
