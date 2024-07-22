package com.study.productmicroservice.core.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Entity
@Getter
@Setter
@Table(name = "products")
public class ProductEntity {

    @Id
    private String productId;

//    @Column(unique = true)
    private String title;
    private BigDecimal price;
    private int quantity;
}
