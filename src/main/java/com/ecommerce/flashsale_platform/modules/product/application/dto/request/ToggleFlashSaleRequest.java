package com.ecommerce.flashsale_platform.modules.product.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ToggleFlashSaleRequest {
    @NotNull
    private Boolean active;
}