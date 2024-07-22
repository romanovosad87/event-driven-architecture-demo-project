package com.study.ordersservice.core.repository;

import com.study.ordersservice.core.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
