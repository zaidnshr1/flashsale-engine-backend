package com.ecommerce.flashsale_platform.modules.product.presentation;

import com.ecommerce.flashsale_platform.common.api.ApiResponse;
import com.ecommerce.flashsale_platform.modules.product.application.dto.request.CreateProductRequest;
import com.ecommerce.flashsale_platform.modules.product.application.dto.response.ProductResponse;
import com.ecommerce.flashsale_platform.modules.product.application.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Endpoints for Product Management and Flash Sale Browsing")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create product & pre-warm cache to Redis (Admin Only)")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Product created and pre-warmed successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details with real-time stock from Redis")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Product retrieved successfully", response));
    }

    @GetMapping("/flash-sale")
    @Operation(summary = "Get all active flash sale products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFlashSaleProducts() {
        List<ProductResponse> response = productService.getActiveFlashSaleProducts();
        return ResponseEntity.ok(ApiResponse.ok("Active flash sale products retrieved", response));
    }
}
