package com.study.ordersservice.core.entity;

import com.study.ordersservice.command.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class OrderEntity {
    @Id
    public String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
}
