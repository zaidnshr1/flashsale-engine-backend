package com.ecommerce.flashsale_platform.modules.cart.application.service;

import com.ecommerce.flashsale_platform.common.exception.ResourceNotFoundException;
import com.ecommerce.flashsale_platform.modules.cart.application.dto.request.AddToCartRequest;
import com.ecommerce.flashsale_platform.modules.cart.application.dto.response.CartItemResponse;
import com.ecommerce.flashsale_platform.modules.cart.application.dto.response.CartResponse;
import com.ecommerce.flashsale_platform.modules.product.domain.model.Product;
import com.ecommerce.flashsale_platform.modules.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    private static final String CART_PREFIX = "cart:";

    public void addToCart(Long userId, AddToCartRequest request) {
        String cartKey = CART_PREFIX + userId;
        redisTemplate.opsForHash().put(cartKey, request.getProductId().toString(), request.getQuantity().toString());
    }

    public CartResponse getCart(Long userId) {
        String cartKey = CART_PREFIX + userId;
        Map<Object, Object> cartEntries = redisTemplate.opsForHash().entries(cartKey);

        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Map.Entry<Object, Object> entry : cartEntries.entrySet()) {
            Long productId = Long.valueOf(entry.getKey().toString());
            Integer quantity = Integer.valueOf(entry.getValue().toString());

            Product product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                BigDecimal subTotal = product.getEffectivePrice().multiply(BigDecimal.valueOf(quantity));
                totalPrice = totalPrice.add(subTotal);

                items.add(CartItemResponse.builder()
                        .productId(productId)
                        .productName(product.getName())
                        .quantity(quantity)
                        .unitPrice(product.getEffectivePrice())
                        .subTotal(subTotal)
                        .build());
            }
        }

        return CartResponse.builder()
                .userId(userId)
                .items(items)
                .totalPrice(totalPrice)
                .build();
    }

    public void removeFromCart(Long userId, Long productId) {
        String cartKey = CART_PREFIX + userId;
        redisTemplate.opsForHash().delete(cartKey, productId.toString());
    }

    public void clearCart(Long userId) {
        String cartKey = CART_PREFIX + userId;
        redisTemplate.delete(cartKey);
    }
}