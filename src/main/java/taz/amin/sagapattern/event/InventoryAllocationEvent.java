package taz.amin.sagapattern.event;

public class InventoryAllocationEvent {
    public enum Type {SUCCESS, FAILURE}

    private final Type eventType;
    private final String orderId;

    public String getOrderId() {
        return orderId;
    }

    public Type getEventType() {
        return eventType;
    }

    public InventoryAllocationEvent() {
        this.eventType = null;
        this.orderId = null;
    }

    public InventoryAllocationEvent(Type eventType, String orderId) {
        this.eventType = eventType;
        this.orderId = orderId;
    }
}
