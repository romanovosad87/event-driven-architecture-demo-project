package com.study.ordersservice.command.controller;

import com.study.ordersservice.command.CreateOrderCommand;
import com.study.ordersservice.command.OrderStatus;
import com.study.ordersservice.core.dto.OrderRequestDto;
import com.study.ordersservice.core.dto.OrderSummary;
import com.study.ordersservice.query.FindOrderQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderCommandController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;


    @PostMapping
    public OrderSummary createOrder(@RequestBody OrderRequestDto dto) {
        String orderId = UUID.randomUUID().toString();
        CreateOrderCommand command
                = new CreateOrderCommand(orderId,
                "27b95829-4f3f-4ddf-8983-151ba010e35b",
                dto.productId(),
                dto.quantity(),
                dto.addressId(),
                OrderStatus.CREATED);

        ResponseType<OrderSummary> responseType = ResponseTypes.instanceOf(OrderSummary.class);
        try (var queryResult = queryGateway.subscriptionQuery(new FindOrderQuery(orderId),
                responseType, responseType)) {
            commandGateway.sendAndWait(command);
            return queryResult.updates().blockFirst();
        }

    }
}
