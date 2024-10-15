package taz.amin.sagapattern.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import taz.amin.sagapattern.event.InventoryAllocationEvent;
import taz.amin.sagapattern.event.OrderPaidEvent;

import java.util.Random;
import java.util.function.Consumer;

@Service
public class InventoryService {
    private final static Logger LOG = LoggerFactory.getLogger(InventoryService.class.getName());
    private final StreamBridge streamBridge;

    @Autowired
    public InventoryService(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Bean
    public Consumer<OrderPaidEvent> orderUpdate() {
        return event -> {
            boolean inventoryAllocated = allocateInventory(event.getOrderId());

            if (inventoryAllocated) {
                LOG.info("Inventory allocated for order: " + event.getOrderId());
            } else {
                // Publish InventoryAllocationFailed event for compensation
                Message message = MessageBuilder.withPayload(new InventoryAllocationEvent(InventoryAllocationEvent.Type.FAILURE, event.getOrderId()))
                        .build();
                LOG.info("Inventory failed for order: " + event.getOrderId());
                streamBridge.send("inventoryUpdate-out-0", message);
            }
        };
    }

    private boolean allocateInventory(String orderId) {
        Random random = new Random();
        boolean randomBoolean = random.nextBoolean();
        LOG.info("allocateInventory [randomBoolean " + randomBoolean +"]");
        return randomBoolean;
    }
}
