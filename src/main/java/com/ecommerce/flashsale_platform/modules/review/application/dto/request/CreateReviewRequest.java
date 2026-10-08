package com.ecommerce.flashsale_platform.modules.review.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateReviewRequest {
    @NotNull(message = "Rating is required")
    @Min(1) @Max(5)
    private Integer rating;
    private String comment;
}