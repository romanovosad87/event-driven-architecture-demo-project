package com.study.productmicroservice.core.errorhandling;

import java.time.LocalDateTime;

public record ErrorMessage(LocalDateTime timestamp, String message) {

}
