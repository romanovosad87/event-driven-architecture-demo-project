package com.study.ordersservice.command;

import com.study.ordersservice.core.event.OrderApprovedEvent;
import com.study.ordersservice.core.event.OrderCreatedEvent;
import com.study.ordersservice.core.event.OrderRejectedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private int quantity;
    private String addressId;
    private OrderStatus orderStatus;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        BeanUtils.copyProperties(command, orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.userId = event.getUserId();
        this.productId = event.getProductId();
        this.quantity = event.getQuantity();
        this.addressId = event.getAddressId();
        this.orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(ApproveOrderCommand command) {
        // create order approve event
        OrderApprovedEvent event = new OrderApprovedEvent(command.getOrderId());

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderApprovedEvent event) {
        orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(RejectOrderCommand command) {
        OrderRejectedEvent event = new OrderRejectedEvent(command.getOrderId(), command.getReason());

        AggregateLifecycle.apply(event);
    }

    @EventSourcingHandler
    public void on(OrderRejectedEvent event) {
        this.orderStatus = event.getOrderStatus();
    }


}
