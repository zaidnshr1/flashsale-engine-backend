package com.ecommerce.flashsale_platform.modules.review.domain.repository;

import com.ecommerce.flashsale_platform.modules.review.domain.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
}