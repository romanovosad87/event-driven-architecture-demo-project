package com.study.ordersservice.saga;

import com.study.core.command.CancelProductReservationCommand;
import com.study.core.command.ProcessPaymentCommand;
import com.study.core.event.PaymentProcessedEvent;
import com.study.core.event.ProductReservationCancelEvent;
import com.study.core.model.User;
import com.study.core.command.ReserveProductCommand;
import com.study.core.event.ProductReservedEvent;
import com.study.core.query.FetchUserPaymentDetailsQuery;
import com.study.ordersservice.command.ApproveOrderCommand;
import com.study.ordersservice.command.RejectOrderCommand;
import com.study.ordersservice.core.dto.OrderSummary;
import com.study.ordersservice.core.event.OrderApprovedEvent;
import com.study.ordersservice.core.event.OrderCreatedEvent;
import com.study.ordersservice.core.event.OrderRejectedEvent;
import com.study.ordersservice.query.FindOrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Saga
public class OrderSaga {

    public static final String PAYMENT_PROCESSING_DEADLINE = "payment-processing-deadline";
    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    @Autowired
    private transient QueryUpdateEmitter queryUpdateEmitter;

    private String scheduleId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand command = ReserveProductCommand.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .productId(orderCreatedEvent.getProductId())
                .userId(orderCreatedEvent.getUserId())
                .quantity(orderCreatedEvent.getQuantity())
                .build();

        log.info("OrderCreatedEvent handled for orderId: {} and productId: {}",
                orderCreatedEvent.getOrderId(), orderCreatedEvent.getProductId());

        commandGateway.send(command, ((commandMessage, commandResultMessage) -> {
            if (commandResultMessage.isExceptional()) {
                RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(orderCreatedEvent.getOrderId(),
                        commandResultMessage.exceptionResult().getMessage());

                commandGateway.send(rejectOrderCommand);
            }
        }));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservedEvent productReservedEvent) {
        // process user payment
        log.info("ProductReservedEvent handled for orderId: {} and productId: {}",
                productReservedEvent.getOrderId(), productReservedEvent.getProductId());

        FetchUserPaymentDetailsQuery query = new FetchUserPaymentDetailsQuery(productReservedEvent.getUserId());
        User user = null;
        try {
            user = queryGateway.query(query, ResponseTypes.instanceOf(User.class)).join();
            log.info("Successfully fetched user payment details for user {}", user.getUserId());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if (user == null) {
            cancelProductReservation(productReservedEvent, "Could not fetch user");
        }

        scheduleId = deadlineManager.schedule(Duration.of(120, ChronoUnit.SECONDS),
                PAYMENT_PROCESSING_DEADLINE,
                productReservedEvent);

        ProcessPaymentCommand processPaymentCommand = ProcessPaymentCommand.builder()
                .orderId(productReservedEvent.getOrderId())
                .paymentDetails(user.getPaymentDetails())
                .paymentId(UUID.randomUUID().toString())
                .build();

        String result = null;
        try {
            result = commandGateway.sendAndWait(processPaymentCommand);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            cancelProductReservation(productReservedEvent, ex.getMessage());
            return;
        }

        if (result == null) {
            log.info("The process payment command resulted in NULL. "
                    + "Initiating a compensating transaction");
            cancelProductReservation(productReservedEvent,
                    "Could not process user payment with provided payment details");

        }
    }

    private void cancelProductReservation(ProductReservedEvent event, String reason) {

        cancelDeadline();

        CancelProductReservationCommand command = CancelProductReservationCommand
                .builder()
                .orderId(event.getOrderId())
                .productId(event.getProductId())
                .quantity(event.getQuantity())
                .userId(event.getUserId())
                .reason(reason)
                .build();
        commandGateway.send(command);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent event) {
        cancelDeadline();
        ApproveOrderCommand command = new ApproveOrderCommand(event.getOrderId());
        commandGateway.send(command);
    }

    private void cancelDeadline() {
        if (scheduleId != null) {
            deadlineManager.cancelSchedule(PAYMENT_PROCESSING_DEADLINE, scheduleId);
            scheduleId = null;
        }
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderApprovedEvent event) {
        log.info("Oder is approved. Order Saga is complete for orderId {}",
                event.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(event.getOrderId(), event.getOrderStatus(), ""));

    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(ProductReservationCancelEvent event) {
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId(),
                event.getReason());

        commandGateway.send(rejectOrderCommand);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderRejectedEvent event) {
        log.info("Successfully rejected order with id {}", event.getOrderId());
        queryUpdateEmitter.emit(FindOrderQuery.class, query -> true,
                new OrderSummary(event.getOrderId(), event.getOrderStatus(), event.getReason()));
    }

    @DeadlineHandler(deadlineName = PAYMENT_PROCESSING_DEADLINE)
    public void handleDeadlinePayment(ProductReservedEvent productReservedEvent) {
        log.info("Payment processing deadline took place. Sending a compensating command to cancel to cancel product reservation");
        cancelProductReservation(productReservedEvent, "Payment timeout");
    }
}
