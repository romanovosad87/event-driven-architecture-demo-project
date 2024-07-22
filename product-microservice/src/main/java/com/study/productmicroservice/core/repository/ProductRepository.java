package com.study.productmicroservice.core.repository;

import com.study.productmicroservice.core.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {

    ProductEntity findByProductId(String productId);

    ProductEntity findByProductIdOrTitle(String productId, String title);
}
