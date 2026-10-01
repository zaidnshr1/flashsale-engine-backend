package com.ecommerce.flashsale_platform.infrastructure.aspect;

import com.ecommerce.flashsale_platform.common.annotation.Idempotent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Slf4j @Aspect @Component @RequiredArgsConstructor
public class IdempotentAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency";
    private static final String STATUS_PROCESSING = "IN_PROGRESS";

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String idempotencyKey = request.getHeader(idempotent.headerName());

        if (!StringUtils.hasText(idempotencyKey)) {
            throw new BadRequestException("Missing mandatory header: " + idempotent.headerName());
        }

        String redisKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;

        Boolean isFirstRequest = redisTemplate.opsForValue().setIfAbsent(redisKey,
                STATUS_PROCESSING,
                Duration.ofSeconds(idempotent.expireSeconds()));

        if (Boolean.FALSE.equals(isFirstRequest)) {
            Object cachedData = redisTemplate.opsForValue().get(redisKey);

            if (STATUS_PROCESSING.equals(cachedData)) {
                log.warn("Concurrent duplicate request detected for key: {}", idempotencyKey);
                throw new BadRequestException("Your previous request is still being processed. Please do not retry yet.");
            }

            log.info("Idempotent hit: Returning cached transaction result for key: {}", idempotencyKey);

            return cachedData;
        }

        try {
            Object result = joinPoint.proceed();
            redisTemplate.opsForValue().set(redisKey, result, Duration.ofSeconds(idempotent.expireSeconds()));

            log.info("Transaction succeeded. Result cached for Idempotency-Key: {}", idempotencyKey);
            return result;
        } catch (Throwable ex) {
            redisTemplate.delete(redisKey);
            log.error("Execution failed for Idempotency-Key: {}. Lock released.", idempotencyKey);
            throw ex;
        }
    }
}
