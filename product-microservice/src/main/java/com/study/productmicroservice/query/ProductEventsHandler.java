package com.study.productmicroservice.query;

import com.study.core.event.ProductReservationCancelEvent;
import com.study.core.event.ProductReservedEvent;
import com.study.productmicroservice.core.entity.ProductEntity;
import com.study.productmicroservice.core.event.ProductCreatedEvent;
import com.study.productmicroservice.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@ProcessingGroup("product-group")
@RequiredArgsConstructor
public class ProductEventsHandler {

    private final ProductRepository productRepository;

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception ex) throws Exception {
        throw ex;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException ex) {
      // Log error message
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        try {
            productRepository.save(productEntity);
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void on(ProductReservedEvent event) {
        ProductEntity entity = productRepository.findByProductId(event.getProductId());
        log.debug("ProductReservedEvent: Current product quantity: {}", entity.getQuantity());
        entity.setQuantity(entity.getQuantity() - event.getQuantity());
        ProductEntity savedEntity = productRepository.save(entity);

        log.debug("ProductReservedEvent: New product quantity: {}", savedEntity.getQuantity());


        log.info("ProductReservedEvent is called for productId: {} and orderId: {}",
                event.getProductId(), event.getOrderId());
    }

    @EventHandler
    public void on(ProductReservationCancelEvent event) {
        ProductEntity entity = productRepository.findByProductId(event.getProductId());
        log.debug("ProductReservationCancelEvent: Current product quantity: {}", entity.getQuantity());

        entity.setQuantity(entity.getQuantity() + event.getQuantity());

        ProductEntity savedEntity = productRepository.save(entity);

        log.debug("ProductReservationCancelEvent: New product quantity: {}", savedEntity.getQuantity());
    }

    @ResetHandler
    public void reset() {
        productRepository.deleteAll();
    }
}
