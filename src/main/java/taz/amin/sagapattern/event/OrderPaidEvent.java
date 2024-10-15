package taz.amin.sagapattern.event;

import org.springframework.core.annotation.Order;

public class OrderPaidEvent {
    public enum Type {SUCCESS, FAILURE}

    public OrderPaidEvent() {
        this.eventType = null;
        this.orderId = null;
        this.userId = null;
    }
    public OrderPaidEvent(Type eventType, String userId, String orderId) {
        this.eventType = eventType;
        this.orderId = orderId;
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public Type getEventType() {
        return eventType;
    }

    private final String orderId;
    private final Type eventType;

    private final String userId;
}
