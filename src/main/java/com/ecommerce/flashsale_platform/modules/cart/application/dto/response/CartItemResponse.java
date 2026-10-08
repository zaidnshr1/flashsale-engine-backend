package com.ecommerce.flashsale_platform.modules.cart.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter @Builder
public class CartItemResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
}