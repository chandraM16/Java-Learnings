import java.util.Arrays;

public class PartA {
    static void applyDiscount(int[] prices, int percent) {
        for (int i = 0; i < prices.length; i++) prices[i] = prices[i] * (100 - percent) / 100;
        percent = 0;
    }

    public static void main(String[] args) {
        int items = 7, boxes = 2;
        double perBox = items / boxes;
        System.out.println("1: " + perBox);

        long ms = 24 * 60 * 60 * 1000 * 45;
        System.out.println("2: " + ms);           // exact number not required: say what KIND of value and why

        String prefix = "ORD-";
        String a = prefix + 0;
        String b = "ORD-0";
        System.out.println("3: " + (a == b) + " " + a.equals(b));

        int[] prices = {1000, 250};
        int percent = 10;
        applyDiscount(prices, percent);
        System.out.println("4: " + Arrays.toString(prices) + " " + percent);

        char grade = 'B';
        System.out.println("5: " + (grade + 1) + " " + (char) (grade + 1) + " " + ("" + grade + 1));

        int code = 2;
        String label = "";
        switch (code) {
            case 1: label += "one ";
            case 2: label += "two ";
            case 3: label += "three ";
                break;
            default: label += "other";
        }
        System.out.println("6: [" + label + "]");

        System.out.println("7: " + (int) -7.9 + " " + (-7 / 2) + " " + (-7 % 2));

        int[] x = {5, 3, 9};
        int[] y = x;
        y = new int[] {1};
        y[0] = 7;
        System.out.println("8: " + Arrays.toString(x));
    }
}
