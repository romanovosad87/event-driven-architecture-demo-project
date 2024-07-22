package com.study.paymentsserivce.event;

import com.study.core.event.PaymentProcessedEvent;
import com.study.paymentsserivce.entity.PaymentEntity;
import com.study.paymentsserivce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventsHandler {
    private final PaymentRepository paymentRepository;

    @EventHandler
    public void on(PaymentProcessedEvent event) {

        log.info("PaymentProcessedEvent is called for orderId: {}", event.getOrderId());

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentId(event.getPaymentId());
        paymentEntity.setOrderId(event.getOrderId());
        paymentRepository.save(paymentEntity);
    }
}
