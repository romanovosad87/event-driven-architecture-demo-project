package com.study.productmicroservice.command;

import com.study.core.command.CancelProductReservationCommand;
import com.study.core.command.ReserveProductCommand;
import com.study.core.event.ProductReservationCancelEvent;
import com.study.core.event.ProductReservedEvent;
import com.study.productmicroservice.core.event.ProductCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import java.math.BigDecimal;

@Aggregate(snapshotTriggerDefinition = "productSnapshotTriggerDefinition")
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private int quantity;

    public ProductAggregate() {
    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {
        if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The price could not be less or equal than zero");
        }

        if (createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()) {
            throw new IllegalArgumentException("The product title can not be empty");
        }

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

        AggregateLifecycle.apply(productCreatedEvent);
    }

    @CommandHandler
    public void handle(ReserveProductCommand command) {
        if (this.quantity < command.getQuantity()) {
            throw new IllegalArgumentException("Insufficient number of items in stock");
        }
        ProductReservedEvent event = ProductReservedEvent.builder()
                .productId(command.getProductId())
                .quantity(command.getQuantity())
                .orderId(command.getOrderId())
                .userId(command.getUserId())
                .build();

        AggregateLifecycle.apply(event);
    }

    @CommandHandler
    public void handle(CancelProductReservationCommand command) {
        ProductReservationCancelEvent event = ProductReservationCancelEvent.builder()
                .orderId(command.getOrderId())
                .productId(command.getProductId())
                .quantity(command.getQuantity())
                .userId(command.getUserId())
                .reason(command.getReason())
                .build();

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(ProductReservationCancelEvent event) {
        this.quantity += event.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent event) {
        this.productId = event.getProductId();
        this.title = event.getTitle();
        this.price = event.getPrice();
        this.quantity = event.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReservedEvent event) {
        this.quantity -= event.getQuantity();
    }
}
