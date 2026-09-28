
import java.util.Arrays;

enum OrderStatus {
    PENDING("Awaiting payment"),
    PAID("Paid"),
    SHIPPED("On the way"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == PAID || next == CANCELLED;
            case PAID -> next == SHIPPED || next == CANCELLED;
            case SHIPPED -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}

public class Test {
    public static void main(String[] args) {

        OrderStatus s1 = OrderStatus.PAID;
        OrderStatus s2 = OrderStatus.PENDING;

        System.out.println(Arrays.toString(OrderStatus.values()));
        System.out.println(s2.ordinal());
    }
}