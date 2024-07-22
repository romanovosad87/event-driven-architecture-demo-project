package com.study.ordersservice.query;

import com.study.ordersservice.core.entity.OrderEntity;
import com.study.ordersservice.core.event.OrderApprovedEvent;
import com.study.ordersservice.core.event.OrderCreatedEvent;
import com.study.ordersservice.core.event.OrderRejectedEvent;
import com.study.ordersservice.core.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("order-group")
@RequiredArgsConstructor
public class OrderEventHandler {

    private final OrderRepository orderRepository;

    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderEntity orderEntity = new OrderEntity();
        BeanUtils.copyProperties(event, orderEntity);
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderApprovedEvent event) {
        OrderEntity orderEntity = orderRepository.findById(event.getOrderId()).orElseThrow();
        orderEntity.setOrderStatus(event.getOrderStatus());
        orderRepository.save(orderEntity);
    }

    @EventHandler
    public void on(OrderRejectedEvent event) {
        OrderEntity orderEntity = orderRepository.findById(event.getOrderId()).orElseThrow();
        orderEntity.setOrderStatus(event.getOrderStatus());
        orderRepository.save(orderEntity);
    }
}
