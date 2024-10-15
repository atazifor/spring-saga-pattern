package taz.amin.sagapattern.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import taz.amin.sagapattern.event.InventoryAllocationEvent;
import taz.amin.sagapattern.event.OrderPaidEvent;

import java.util.function.Consumer;

@Service
public class AccountService {
    private final static Logger LOG = LoggerFactory.getLogger(AccountService.class.getName());
    @Bean
    public Consumer<OrderPaidEvent>orderUpdateAccount() {
        return event -> {
            if(event.getEventType().equals(OrderPaidEvent.Type.FAILURE)) {
                refundBalance(event.getOrderId());
                LOG.info("Successfully Refunded balance [BECAUSE OF order update failure] for order: " + event.getOrderId());
            }
        };
    }

    @Bean
    public Consumer<InventoryAllocationEvent> inventoryUpdate() {
        return event -> {
            if(event.getEventType().equals(InventoryAllocationEvent.Type.FAILURE)) {
                refundBalance(event.getOrderId());
                LOG.info("Successfully Refunded balance [BECAUSE OF inventory failure] for order: " + event.getOrderId());
            }
        };
    }

    private void refundBalance(String userId) {
        // Simulate balance refund logic
        LOG.info("Balance refunded for user: " + userId);
    }
}
