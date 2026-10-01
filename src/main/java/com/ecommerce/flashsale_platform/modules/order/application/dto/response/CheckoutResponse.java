package com.ecommerce.flashsale_platform.modules.order.application.dto.response;

import com.ecommerce.flashsale_platform.modules.order.domain.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class CheckoutResponse {

    private String orderNumber;
    private OrderStatus status;
    private BigDecimal estimatedTotal;
    private String message;
}
