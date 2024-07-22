package com.study.productmicroservice;

import com.study.productmicroservice.command.interceptor.CreateProductCommandInterceptor;
import com.study.productmicroservice.config.AxonConfig;
import com.study.productmicroservice.core.errorhandling.ProductServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@EnableDiscoveryClient
@SpringBootApplication
@Import({ AxonConfig.class })
public class ProductMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductMicroserviceApplication.class, args);
    }

    @Autowired
    public void registerProductsCommandInterceptor(ApplicationContext context,
                                                          CommandBus commandBus) {
        commandBus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));
    }

    @Autowired
    public void configure(EventProcessingConfigurer configurer) {
        configurer.registerListenerInvocationErrorHandler("product-group",
                configuration -> new ProductServiceEventsErrorHandler());
    }
}
