package com.study.productmicroservice.query;

import com.study.productmicroservice.core.repository.ProductRepository;
import com.study.productmicroservice.query.controller.ProductRestModel;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductsQueryHandler {

    private final ProductRepository productRepository;

    @QueryHandler
    @Transactional(readOnly = true)
    public List<ProductRestModel> findProducts(FindProductsQuery query) {

        return productRepository.findAll()
                .stream()
                .map(productEntity -> {
                    ProductRestModel productRestModel = new ProductRestModel();
                    BeanUtils.copyProperties(productEntity, productRestModel);
                    return productRestModel;
                })
                .toList();
    }
}
