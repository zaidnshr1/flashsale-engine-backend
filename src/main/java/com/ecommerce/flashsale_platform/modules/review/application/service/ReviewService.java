package com.ecommerce.flashsale_platform.modules.review.application.service;

import com.ecommerce.flashsale_platform.modules.review.application.dto.request.CreateReviewRequest;
import com.ecommerce.flashsale_platform.modules.review.application.dto.response.ReviewResponse;
import com.ecommerce.flashsale_platform.modules.review.domain.model.Review;
import com.ecommerce.flashsale_platform.modules.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public ReviewResponse addReview(Long userId, Long productId, CreateReviewRequest request) {
        Review review = Review.builder()
                .userId(userId)
                .productId(productId)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}