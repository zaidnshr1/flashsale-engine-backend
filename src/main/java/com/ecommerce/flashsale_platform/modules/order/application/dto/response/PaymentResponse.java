package com.ecommerce.flashsale_platform.modules.order.application.dto.response;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class PaymentResponse implements Serializable {

    private String transactionId;
    private String orderNumber;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private String paymentStatus;
    private Instant paidAt;
}
