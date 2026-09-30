package com.ecommerce.flashsale_platform.modules.order.infrastructure.messaging;

import com.ecommerce.flashsale_platform.modules.order.application.message.OrderMessage;
import com.ecommerce.flashsale_platform.modules.order.domain.model.Order;
import com.ecommerce.flashsale_platform.modules.order.domain.model.OrderStatus;
import com.ecommerce.flashsale_platform.modules.order.domain.repository.OrderRepository;
import com.ecommerce.flashsale_platform.modules.product.application.service.ProductService;
import com.ecommerce.flashsale_platform.modules.product.domain.model.Product;
import com.ecommerce.flashsale_platform.modules.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j @RequiredArgsConstructor @Component
public class OrderConsumer {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    @Transactional
    public void consumeOrderMessage(OrderMessage message) {
        log.info("Worker received order message from queue: {}", message.getOrderNumber());

        try {
            Product product = productRepository.findById(message.getProductId())
                    .orElseThrow(() -> new IllegalStateException("Product not found: " + message.getProductId()));

            product.decreaseStock(message.getQuantity());
            productRepository.save(product);

            BigDecimal totalPrice = message.getPrice().multiply(BigDecimal.valueOf(message.getQuantity()));
            Order order = Order.builder()
                    .orderNumber(message.getOrderNumber())
                    .userId(message.getUserId())
                    .productId(message.getProductId())
                    .quantity(message.getQuantity())
                    .totalPrice(totalPrice)
                    .status(OrderStatus.SUCCESS)
                    .build();

            orderRepository.save(order);
            log.info("Order [{}] successfully processed and persisted to DB.", message.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to process order [{}]. Error: {}. Executing Redis compensation...",
                    message.getOrderNumber(), e.getMessage());

            String stockKey = ProductService.REDIS_STOK_KEY_PREFIX + message.getOrderNumber();
            redisTemplate.opsForValue().increment(stockKey, message.getQuantity());

            Order failedOrder = Order.builder()
                    .orderNumber(message.getOrderNumber())
                    .userId(message.getUserId())
                    .productId(message.getProductId())
                    .quantity(message.getQuantity())
                    .totalPrice(BigDecimal.ZERO)
                    .status(OrderStatus.FAILED)
                    .build();

            orderRepository.save(failedOrder);
        }

    }
}
