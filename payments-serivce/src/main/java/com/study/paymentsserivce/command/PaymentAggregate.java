package com.study.paymentsserivce.command;

import com.study.core.command.ProcessPaymentCommand;
import com.study.core.event.PaymentProcessedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class PaymentAggregate {

    @AggregateIdentifier
    private String paymentId;
    private String orderId;


    public  PaymentAggregate() {
    }

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand command) {
        if(command.getPaymentDetails() == null) {
            throw new IllegalArgumentException("Missing payment details");
        }

        if(command.getOrderId() == null) {
            throw new IllegalArgumentException("Missing orderId");
        }

        if(command.getPaymentId() == null) {
            throw new IllegalArgumentException("Missing paymentId");
        }

        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .orderId(command.getOrderId())
                .paymentId(command.getPaymentId())
                .build();

        AggregateLifecycle.apply(event);

    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.orderId = event.getOrderId();
        this.paymentId = event.getPaymentId();
    }

}
