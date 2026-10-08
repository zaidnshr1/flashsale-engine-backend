package com.ecommerce.flashsale_platform.modules.order.presentation;

import com.ecommerce.flashsale_platform.common.api.ApiResponse;
import com.ecommerce.flashsale_platform.modules.order.application.dto.request.UpdateOrderStatusRequest;
import com.ecommerce.flashsale_platform.modules.order.application.service.OrderService;
import com.ecommerce.flashsale_platform.modules.order.domain.model.Order;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Order Management", description = "Admin endpoints for managing orders")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.ok("All orders retrieved", orderService.getAllOrders()));
    }

    @PatchMapping("/{orderNumber}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable String orderNumber, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Order status updated", orderService.updateOrderStatus(orderNumber, request.getStatus())));
    }
}