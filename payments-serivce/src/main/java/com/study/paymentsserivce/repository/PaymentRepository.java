package com.study.paymentsserivce.repository;

import com.study.paymentsserivce.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
}
