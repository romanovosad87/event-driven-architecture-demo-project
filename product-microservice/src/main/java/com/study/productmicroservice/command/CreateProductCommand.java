package com.study.productmicroservice.command;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateProductCommand {
    private final String productId;
    private final String title;
    private final BigDecimal price;
    private final int quantity;
}
