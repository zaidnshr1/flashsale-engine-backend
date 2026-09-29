package com.ecommerce.flashsale_platform.modules.product.domain.repository;

import com.ecommerce.flashsale_platform.modules.product.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByFlashSaleActive();
}
