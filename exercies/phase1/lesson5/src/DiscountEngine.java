public class DiscountEngine {
    public static void main(String[] args) {
        String tier = args.length > 0 ? args[0] : "silver";
        int itemCount = 3;
        boolean isPremium = false;

        int discountPercent = switch (tier) {
            case "gold" ->  20;
            case "silver" ->  10;
            case "bronze" ->  5;
            default -> 0;
        };
        
        if (isPremium) {
            discountPercent = discountPercent + 5;
        }

        int failed = 1;
        double failureRate = (double )failed / itemCount * 100;

        System.out.println("Tier: " + tier + ", discount: " + discountPercent + "%");
        System.out.println("Premium: " + isPremium);
        System.out.println("Failure rate: " + String.format("%.2f", failureRate) + "%");
    }
}
