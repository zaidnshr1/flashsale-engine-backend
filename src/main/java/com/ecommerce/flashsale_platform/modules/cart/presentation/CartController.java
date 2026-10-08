package com.ecommerce.flashsale_platform.modules.cart.presentation;

import com.ecommerce.flashsale_platform.common.api.ApiResponse;
import com.ecommerce.flashsale_platform.infrastructure.security.UserPrincipal;
import com.ecommerce.flashsale_platform.modules.cart.application.dto.request.AddToCartRequest;
import com.ecommerce.flashsale_platform.modules.cart.application.dto.response.CartResponse;
import com.ecommerce.flashsale_platform.modules.cart.application.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Shopping Cart", description = "Redis-based shopping cart operations")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<Void>> addToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Cart retrieved", cartService.getCart(principal.getId())));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        cartService.removeFromCart(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }
}