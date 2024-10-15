package taz.amin.sagapattern.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import taz.amin.sagapattern.event.BalanceDeductedEvent;
import taz.amin.sagapattern.event.OrderPaidEvent;
import taz.amin.sagapattern.exception.PaymentUpdateException;

import java.util.Random;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

@Service
public class OrderService {
    private final static Logger LOG = LoggerFactory.getLogger(OrderService.class.getName());
    private final StreamBridge streamBridge;

    @Autowired
    public OrderService(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Bean
    public Consumer<BalanceDeductedEvent> balanceUpdate() {
        return event -> {
            String status = paymentStatus();

            boolean orderUpdated = updateOrderStatus(event.getOrderId(), status);
            if(orderUpdated) {
                // Publish OrderPaid event
                LOG.info("Order successfully updated");
                Message message = createOrderPaidEventMessage(OrderPaidEvent.Type.SUCCESS, event);
                streamBridge.send("orderUpdate-out-0", message);
            }else {
                // Publish PaymentFailed event for compensation
                LOG.info("Order failed to update");
                Message message = createOrderPaidEventMessage(OrderPaidEvent.Type.FAILURE, event);
                streamBridge.send("orderUpdate-out-0", message);
            }
        };
    }

    private boolean updateOrderStatus(String orderId, String status) {
        LOG.info("updateOrderStatus called");
        Random random = new Random();
        boolean randomBoolean = random.nextBoolean();
        LOG.info("updateOrderStatus [randomBoolean " + randomBoolean +"]");
        return randomBoolean;
    }

    private Message createOrderPaidEventMessage(OrderPaidEvent.Type eventType, BalanceDeductedEvent event) {
        return MessageBuilder.withPayload(new OrderPaidEvent(eventType, event.getUserId(), event.getOrderId()))
                .build();
    }

    private String paymentStatus() {
        Random random = new Random();
        int randomNumber = random.nextInt(3) + 1;
        String status = "Paid";
        switch (randomNumber) {
            case 1:
                status = "Paid";
                break;
            case 2:
                status = "Overdrawn";
                break;
            default:
                status = "Paid";
        }
        return status;
    }
}
