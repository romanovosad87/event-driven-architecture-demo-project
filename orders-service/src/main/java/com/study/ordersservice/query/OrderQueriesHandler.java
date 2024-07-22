package com.study.ordersservice.query;

import com.study.ordersservice.core.dto.OrderSummary;
import com.study.ordersservice.core.entity.OrderEntity;
import com.study.ordersservice.core.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OrderQueriesHandler {

    private final OrderRepository orderRepository;

    @QueryHandler
    public OrderSummary findOrder(FindOrderQuery query) {
        OrderEntity orderEntity = orderRepository.findById(query.getOrderId()).orElseThrow();
        return new OrderSummary(orderEntity.orderId, orderEntity.getOrderStatus(), "");
    }
}
