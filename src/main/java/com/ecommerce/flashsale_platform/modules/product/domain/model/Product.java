package com.ecommerce.flashsale_platform.modules.product.domain.model;

import com.ecommerce.flashsale_platform.common.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "products", indexes = {
        @Index(name = "idx_products_flash_sale", columnList = "flash_sale_active")})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name= "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "flash_sale_price", nullable = false)
    private BigDecimal flashSalePrice;

    @Column(name = "stok", nullable = false)
    private Integer stok;

    @Builder.Default
    @Column(name = "flash_sale_active", nullable = false)
    private boolean flashSaleActive = false;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt =  Instant.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }


    public boolean hasStock(int quantity) {
        return this.stok != null && stok >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (!hasStock(quantity)) {
            throw new BadRequestException("Insufficient stock for product: " + this.name);
        }
        this.stok =- quantity;
    }

    public BigDecimal getEffectivePrice() {
        return this.flashSaleActive ? this.flashSalePrice : this.price;
    }
}
