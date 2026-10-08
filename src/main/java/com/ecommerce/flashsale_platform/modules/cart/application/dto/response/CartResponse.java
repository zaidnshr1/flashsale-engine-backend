package com.ecommerce.flashsale_platform.modules.cart.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
public class CartResponse {
    private Long userId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
}