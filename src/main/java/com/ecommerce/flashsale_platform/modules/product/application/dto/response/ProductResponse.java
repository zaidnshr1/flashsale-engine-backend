package com.ecommerce.flashsale_platform.modules.product.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal regularPrice;
    private BigDecimal flashSalePrice;
    private BigDecimal effectivePrice;
    private Integer stock;
    private boolean flashSaleActive;
}
