package com.ecommerce.flashsale_platform.modules.product.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Flash sale price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Flash sale price must be greater than 0")
    private BigDecimal flashSalePrice;

    @NotNull(message = "Initial stock is required")
    @Min(value = 1, message = "Initial stock must be at least 1")
    private Integer stock;

    private boolean flashSaleActive = true;
}
