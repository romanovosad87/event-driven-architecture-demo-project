package com.study.usersservice;

import com.study.core.model.PaymentDetails;
import com.study.core.model.User;
import com.study.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserEventsHandler {

    @QueryHandler
    public User handle(FetchUserPaymentDetailsQuery query) {
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("SERGEY KARGOPOLOV")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        return User.builder()
                .firstName("Sergey")
                .lastName("Kargopolov")
                .userId(query.getUserId())
                .paymentDetails(paymentDetails)
                .build();
    }
}
