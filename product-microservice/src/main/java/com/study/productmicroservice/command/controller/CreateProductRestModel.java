package com.study.productmicroservice.command.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateProductRestModel (
        @NotBlank(message = "Product title is a required field")
        String title,
        @Min(value = 1, message = "Price cannot be less than 1")
        BigDecimal price,
        @Min(value = 1, message = "Quantity cannot be less than 1")
        @Max(value = 5, message = "Quantity cannot be larger than 5")
        int quantity){
}
