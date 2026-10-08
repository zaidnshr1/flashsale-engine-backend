package com.ecommerce.flashsale_platform.modules.review.application.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter @Builder
public class ReviewResponse {
    private Long id;
    private Long userId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}