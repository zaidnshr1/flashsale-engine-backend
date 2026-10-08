package com.ecommerce.flashsale_platform.modules.order.application.dto.request;

import com.ecommerce.flashsale_platform.modules.order.domain.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateOrderStatusRequest {
    @NotNull
    private OrderStatus status;
}