package com.ecommerce.flashsale_platform.modules.order.application.service;

import com.ecommerce.flashsale_platform.common.exception.BadRequestException;
import com.ecommerce.flashsale_platform.common.exception.ResourceNotFoundException;
import com.ecommerce.flashsale_platform.modules.order.application.dto.request.PaymentRequest;
import com.ecommerce.flashsale_platform.modules.order.application.dto.response.PaymentResponse;
import com.ecommerce.flashsale_platform.modules.order.domain.model.Order;
import com.ecommerce.flashsale_platform.modules.order.domain.model.OrderStatus;
import com.ecommerce.flashsale_platform.modules.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j @Service @RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse payOrder(String orderNumber, PaymentRequest request) throws BadRequestException {
        log.info("Processing financial payment for Order: {}", orderNumber);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new BadRequestException("Order is already paid!");
        }

        if (order.getStatus() != OrderStatus.SUCCESS) {
            throw new BadRequestException("Order cannot be paid in current status: " + order.getStatus());
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        String transactionId = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.info("Payment confirmed! Transaction ID: {} for Order: {}", transactionId, orderNumber);

        return PaymentResponse.builder()
                .transactionId(transactionId)
                .orderNumber(order.getOrderNumber())
                .amountPaid(order.getTotalPrice())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("SETTLEMENT_SUCCESS")
                .paidAt(Instant.now())
                .build();
    }
}
