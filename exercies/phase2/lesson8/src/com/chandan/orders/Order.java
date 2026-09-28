package com.chandan.orders;

public class Order {
    private final String id;
    private OrderStatus status = OrderStatus.PENDING;

    public Order(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void moveTo(OrderStatus next) {
        if (!status.canMoveTo(next)) {
            throw new IllegalStateException("Cannot move from " + status + " to " + next);
        }
        status = next;
    }

    public static void main(String[] args) {
        Order o = new Order("123");
        o.moveTo(OrderStatus.PAID);
        System.out.println(o.getStatus().getLabel());
        o.moveTo(OrderStatus.SHIPPED);
        System.out.println(o.getStatus().getLabel());
        o.moveTo(OrderStatus.DELIVERED);
        System.out.println(o.getStatus().getLabel());

        System.out.println(OrderStatus.parse("paid"));
        System.out.println(OrderStatus.parse(" shipped "));

        try {
            o.moveTo(OrderStatus.PAID);
        } catch (IllegalStateException e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
        }
    }
}
