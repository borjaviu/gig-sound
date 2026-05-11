package com.gigsound.infrastructure.api.controller;

import com.gigsound.application.dto.*;
import com.gigsound.application.usecase.CancelOrderUseCase;
import com.gigsound.application.usecase.GetEventSalesUseCase;
import com.gigsound.application.usecase.PurchaseOrderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final PurchaseOrderUseCase purchaseOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final GetEventSalesUseCase getEventSalesUseCase;

    public OrderController(PurchaseOrderUseCase purchaseOrderUseCase,
                           CancelOrderUseCase cancelOrderUseCase,
                           GetEventSalesUseCase getEventSalesUseCase) {
        this.purchaseOrderUseCase = purchaseOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.getEventSalesUseCase = getEventSalesUseCase;
    }

    /**
     * POST /orders/purchase
     * Purchase tickets for an event.
     */
    @PostMapping("/purchase")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse purchaseOrder(@Valid @RequestBody PurchaseOrderRequest request) {
        return purchaseOrderUseCase.execute(request);
    }

    /**
     * DELETE /orders/cancel
     * Cancel an existing order.
     */
    @DeleteMapping("/cancel")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse cancelOrder(@Valid @RequestBody CancelOrderRequest request) {
        return cancelOrderUseCase.execute(request);
    }

    /**
     * GET /orders/event/{eventId}/sales
     * Get all sales (orders) for a specific event.
     */
    @GetMapping("/event/{eventId}/sales")
    @ResponseStatus(HttpStatus.OK)
    public EventSalesResponse getEventSales(@PathVariable UUID eventId) {
        return getEventSalesUseCase.execute(eventId);
    }
}
