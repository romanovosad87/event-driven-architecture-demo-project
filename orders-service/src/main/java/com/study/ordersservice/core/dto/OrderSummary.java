package com.study.ordersservice.core.dto;

import com.study.ordersservice.command.OrderStatus;
import lombok.Value;

@Value
public class OrderSummary {
    private final String orderId;
    private final OrderStatus orderStatus;
    private final String message;
}
