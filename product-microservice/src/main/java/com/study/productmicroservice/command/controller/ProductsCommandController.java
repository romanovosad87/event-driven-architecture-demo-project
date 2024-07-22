package com.study.productmicroservice.command.controller;

import com.study.productmicroservice.command.CreateProductCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductsCommandController {

    private final Environment env;
    private final CommandGateway commandGateway;

    @PostMapping
    public String creatProduct(@RequestBody @Valid CreateProductRestModel model) {
        String productId = UUID.randomUUID().toString();
        CreateProductCommand createProductCommand = new CreateProductCommand(productId,
                model.title(), model.price(), model.quantity());

        String returnValue;

        returnValue = commandGateway.sendAndWait(createProductCommand);

//        try {
//            returnValue = commandGateway.sendAndWait(createProductCommand);
//        } catch (Exception exception) {
//            log.error("Failed to dispatch command: {}", exception.getMessage(), exception);
//            returnValue = exception.getLocalizedMessage();
//        }

        return returnValue;
    }

//    @GetMapping
//    public String getProduct() {
//        var port = env.getProperty("local.server.port");
//        return "HTTP GET Handled. Port: " + port;
//    }

//    @PutMapping
//    public String updateProduct() {
//        return "HTTP PUT Handled";
//    }
//
//    @DeleteMapping
//    public String deleteProduct() {
//        return "HTTP DELETE Handled";
//    }
}
