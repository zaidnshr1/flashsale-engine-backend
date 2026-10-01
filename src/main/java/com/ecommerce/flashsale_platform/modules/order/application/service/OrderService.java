package com.ecommerce.flashsale_platform.modules.order.application.service;

import com.ecommerce.flashsale_platform.common.exception.BadRequestException;
import com.ecommerce.flashsale_platform.common.exception.ResourceNotFoundException;
import com.ecommerce.flashsale_platform.modules.order.application.dto.request.CheckoutRequest;
import com.ecommerce.flashsale_platform.modules.order.application.dto.response.CheckoutResponse;
import com.ecommerce.flashsale_platform.modules.order.application.message.OrderMessage;
import com.ecommerce.flashsale_platform.modules.order.domain.model.Order;
import com.ecommerce.flashsale_platform.modules.order.domain.model.OrderStatus;
import com.ecommerce.flashsale_platform.modules.order.domain.repository.OrderRepository;
import com.ecommerce.flashsale_platform.modules.product.application.service.ProductService;
import com.ecommerce.flashsale_platform.modules.product.domain.model.Product;
import com.ecommerce.flashsale_platform.modules.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service @Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Transactional
    public CheckoutResponse processFlashSaleCheckout(Long userId, CheckoutRequest request) {
        Long productId = request.getProductId();
        int quantity = request.getQuantity();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (!product.isFlashSaleActive()) {
            throw new BadRequestException("Product is not currently available for flash sale");
        }

        String stockKey = ProductService.REDIS_STOK_KEY_PREFIX + productId;
        Long remainingStok = redisTemplate.opsForValue().decrement(stockKey, quantity);

        log.info("Redis atomic decrement for product {}. Remaining in Redis: {}", productId, remainingStok);

        if (remainingStok == null || remainingStok < 0) {
            redisTemplate.opsForValue().increment(stockKey, quantity);
            log.warn("Flash sale stock sold out for product ID: {}. Rollback executed.", productId);
            throw new BadRequestException("Flash sale stock is sold out or insufficient!");
        }

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        BigDecimal price = product.getEffectivePrice();

        OrderMessage orderMessage = OrderMessage.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .createdAt(Instant.now())
                .build();

        rabbitTemplate.convertAndSend(exchangeName, routingKey, orderMessage);
        log.info("Order message dispatched to RabbitMQ Exchange [{}]: {}", exchangeName, orderNumber);

        return CheckoutResponse.builder()
                .orderNumber(orderNumber)
                .status(OrderStatus.PENDING)
                .estimatedTotal(price.multiply(BigDecimal.valueOf(quantity)))
                .message("Your order has been queued successfully. We are processing it!")
                .build();

    }

    @Transactional(readOnly = true)
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
    }
}