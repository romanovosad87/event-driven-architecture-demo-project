package com.study.ordersservice.core.event;

import com.study.ordersservice.command.OrderStatus;
import lombok.Value;

@Value
public class OrderRejectedEvent {
    private final String orderId;
    private final String reason;
    private final OrderStatus orderStatus = OrderStatus.REJECTED;
}
