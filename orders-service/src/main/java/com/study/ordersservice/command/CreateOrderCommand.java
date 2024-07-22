package com.study.ordersservice.command;

import lombok.Data;

@Data
public class CreateOrderCommand {
    public final String orderId;
    private final String userId;
    private final String productId;
    private final int quantity;
    private final String addressId;
    private final OrderStatus orderStatus;
}

