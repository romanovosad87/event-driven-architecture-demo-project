package com.study.ordersservice.core.event;

import com.study.ordersservice.command.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {
    private final String orderId;
    private final OrderStatus orderStatus = OrderStatus.APPROVED;
}
