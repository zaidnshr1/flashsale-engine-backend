package com.ecommerce.flashsale_platform.modules.order.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentRequest {

    @NotBlank(message = "Payment method is required (e.g. CREDIT_CARD, GOPAY, VA)")
    private String paymentMethod;
}
