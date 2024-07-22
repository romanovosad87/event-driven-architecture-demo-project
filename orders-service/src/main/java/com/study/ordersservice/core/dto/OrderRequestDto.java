package com.study.ordersservice.core.dto;

public record OrderRequestDto(String productId,
                              int quantity,
                              String addressId) {
}
