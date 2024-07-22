package com.study.productmicroservice.command;

import com.study.productmicroservice.core.entity.ProductLookupEntity;
import com.study.productmicroservice.core.event.ProductCreatedEvent;
import com.study.productmicroservice.core.repository.ProductLookupRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
@RequiredArgsConstructor
public class ProductLookupEventsHandler {

    private final ProductLookupRepository productLookupRepository;

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductLookupEntity entity = new ProductLookupEntity();
        entity.setProductId(event.getProductId());
        entity.setTitle(event.getTitle());
        productLookupRepository.save(entity);
    }

    @ResetHandler
    public void reset() {
        productLookupRepository .deleteAll();
    }
}
