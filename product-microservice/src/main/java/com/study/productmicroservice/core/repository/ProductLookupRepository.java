package com.study.productmicroservice.core.repository;

import com.study.productmicroservice.core.entity.ProductLookupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLookupRepository extends JpaRepository<ProductLookupEntity, String> {
    ProductLookupEntity findByProductIdOrTitle(String productId, String title);
}
