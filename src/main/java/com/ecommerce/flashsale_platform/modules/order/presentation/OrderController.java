package com.ecommerce.flashsale_platform.modules.order.presentation;

import com.ecommerce.flashsale_platform.common.api.ApiResponse;
import com.ecommerce.flashsale_platform.infrastructure.security.UserPrincipal;
import com.ecommerce.flashsale_platform.modules.order.application.dto.request.CheckoutRequest;
import com.ecommerce.flashsale_platform.modules.order.application.dto.response.CheckoutResponse;
import com.ecommerce.flashsale_platform.modules.order.application.service.OrderService;
import com.ecommerce.flashsale_platform.modules.order.domain.model.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "Order & Checkout", description = "High-performance Flash Sale Checkout Endpoints")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Fast Flash Sale Checkout (Atomic Redis + RabbitMQ)")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CheckoutRequest request) {

        CheckoutResponse response = orderService.processFlashSaleCheckout(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.ok("Checkout initiated", response));
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Check order processing status")
    public ResponseEntity<ApiResponse<Order>> getOrderStatus(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.ok("Order status retrieved", order));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<java.util.List<Order>>> getUserOrders(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("User orders retrieved", orderService.getUserOrders(principal.getId())));
    }

    @PostMapping("/{orderNumber}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable String orderNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled", orderService.cancelOrder(orderNumber, principal.getId())));
    }
}
