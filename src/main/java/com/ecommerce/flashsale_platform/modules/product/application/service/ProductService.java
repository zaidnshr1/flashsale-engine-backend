package com.ecommerce.flashsale_platform.modules.product.application.service;

import com.ecommerce.flashsale_platform.common.exception.ResourceNotFoundException;
import com.ecommerce.flashsale_platform.modules.product.application.dto.request.CreateProductRequest;
import com.ecommerce.flashsale_platform.modules.product.application.dto.response.ProductResponse;
import com.ecommerce.flashsale_platform.modules.product.domain.model.Product;
import com.ecommerce.flashsale_platform.modules.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j @RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public static final String REDIS_STOK_KEY_PREFIX = "product:stok:";

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .regularPrice(product.getPrice())
                .flashSalePrice(product.getFlashSalePrice())
                .effectivePrice(product.getEffectivePrice())
                .stock(product.getStok())
                .flashSaleActive(product.isFlashSaleActive())
                .build();
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .flashSalePrice(request.getFlashSalePrice())
                .stok(request.getStock())
                .flashSaleActive(request.isFlashSaleActive())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product saved in PostgreSQL with ID: {}", savedProduct.getId());

        String redisKey = REDIS_STOK_KEY_PREFIX + product.getId();
        redisTemplate.opsForValue().set(redisKey, savedProduct.getStok());
        log.info("Redis cache pre-warmed for key: {} with stock: {}", redisKey, savedProduct.getStok());

        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        String redisKey = REDIS_STOK_KEY_PREFIX + productId;
        Object cachedStok = redisTemplate.opsForValue().get(redisKey);
        int currentStok = (cachedStok != null) ? Integer.parseInt(cachedStok.toString()) : product.getStok();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .regularPrice(product.getPrice())
                .flashSalePrice(product.getFlashSalePrice())
                .effectivePrice(product.getEffectivePrice())
                .stock(currentStok)
                .flashSaleActive(product.isFlashSaleActive())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveFlashSaleProducts() {
        return productRepository.findByFlashSaleActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ProductResponse updateProduct(Long id, com.ecommerce.flashsale_platform.modules.product.application.dto.request.UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getFlashSalePrice() != null) product.setFlashSalePrice(request.getFlashSalePrice());
        if (request.getStock() != null) {
            product.setStok(request.getStock());
            redisTemplate.opsForValue().set(REDIS_STOK_KEY_PREFIX + id, request.getStock());
        }

        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
        redisTemplate.delete(REDIS_STOK_KEY_PREFIX + id);
    }

    @Transactional
    public ProductResponse toggleFlashSale(Long id, com.ecommerce.flashsale_platform.modules.product.application.dto.request.ToggleFlashSaleRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setFlashSaleActive(request.getActive());
        return mapToResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }
}
