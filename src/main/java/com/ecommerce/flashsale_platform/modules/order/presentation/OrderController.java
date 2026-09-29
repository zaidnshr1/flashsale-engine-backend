package com.ecommerce.flashsale_platform.modules.order.presentation;

import com.ecommerce.flashsale_platform.common.api.ApiResponse;
import com.ecommerce.flashsale_platform.infrastructure.security.UserPrincipal;
import com.ecommerce.flashsale_platform.modules.order.application.dto.CheckoutRequest;
import com.ecommerce.flashsale_platform.modules.order.application.dto.CheckoutResponse;
import com.ecommerce.flashsale_platform.modules.order.application.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
