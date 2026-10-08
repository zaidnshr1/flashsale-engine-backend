package com.ecommerce.flashsale_platform.modules.review.presentation;

import com.ecommerce.flashsale_platform.common.api.ApiResponse;
import com.ecommerce.flashsale_platform.infrastructure.security.UserPrincipal;
import com.ecommerce.flashsale_platform.modules.review.application.dto.request.CreateReviewRequest;
import com.ecommerce.flashsale_platform.modules.review.application.dto.response.ReviewResponse;
import com.ecommerce.flashsale_platform.modules.review.application.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Product Reviews", description = "Endpoints for product reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> addReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.addReview(principal.getId(), productId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Review added", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.ok("Reviews retrieved", reviewService.getProductReviews(productId)));
    }
}