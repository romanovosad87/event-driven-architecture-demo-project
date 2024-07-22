package com.study.productmicroservice.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "productlookup")
public class ProductLookupEntity {

    @Id
    private String productId;
    @Column(unique = true)
    private String title;
}
