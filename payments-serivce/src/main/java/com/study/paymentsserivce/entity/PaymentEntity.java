package com.study.paymentsserivce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "payments")
public class PaymentEntity {
    @Id
    private String paymentId;
    private String orderId;
}
