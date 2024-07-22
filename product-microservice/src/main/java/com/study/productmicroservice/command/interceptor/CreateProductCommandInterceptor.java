package com.study.productmicroservice.command.interceptor;

import com.study.productmicroservice.command.CreateProductCommand;
import com.study.productmicroservice.core.entity.ProductLookupEntity;
import com.study.productmicroservice.core.repository.ProductLookupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private final ProductLookupRepository productLookupRepository;

    @Transactional(readOnly = true)
    @Nonnull
    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            @Nonnull List<? extends CommandMessage<?>> messages) {

        return (index, command) -> {
            log.info("Interceptor command: {}", command.getPayload());
            if (command.getPayload() instanceof CreateProductCommand createProductCommand) {
                String productId = createProductCommand.getProductId();
                String title = createProductCommand.getTitle();
                ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(productId,
                        title);

                if (productLookupEntity != null) {
                    throw new IllegalStateException("Product with the product id %s or title %s already exists."
                            .formatted(productId, title));
                }

            }
            return command;
        };
    }
}
