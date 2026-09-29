package com.ecommerce.flashsale_platform.modules.order.application.message;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class OrderMessage {

    private String orderNumber;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private Instant createdAt;
}
