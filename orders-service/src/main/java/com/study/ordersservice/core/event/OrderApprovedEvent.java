package com.study.ordersservice.core.event;

import com.study.ordersservice.command.OrderStatus;
import lombok.Value;

@Value
public class OrderApprovedEvent {
     String orderId;
     OrderStatus orderStatus = OrderStatus.APPROVED;
}
