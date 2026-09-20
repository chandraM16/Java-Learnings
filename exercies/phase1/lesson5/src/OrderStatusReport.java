public class OrderStatusReport {
    public static void main(String[] args) {
        if(args.length == 0){
            System.out.println("Please provide some arguments");
            return;
        }

        int noOfPaidOrders = 0;
        int noOfPendingOrders = 0;
        int noOfShippedOrders = 0;
        int noOfCancelledOrders = 0;
        int noOfUnknownOrderStatus = 0;

        for (String orderStatusString : args) {
            switch (orderStatusString.toLowerCase()) {
                case "paid" -> noOfPaidOrders++;
                case "pending" -> noOfPendingOrders++;
                case "cancelled" -> noOfCancelledOrders++;
                case "shipped" -> noOfShippedOrders++;
                default -> noOfUnknownOrderStatus++;
            } 
        }

        double paidOrdersInPer = (double) noOfPaidOrders / args.length * 100;

        System.out.println(args.length + " orders processed, " + noOfPaidOrders + " paid (" + String.format("%.2f", paidOrdersInPer) + "%), " + noOfUnknownOrderStatus + " unrecognised");
    }
}
