public class PassByValue {
    static void addTax(int priceInPaise) {
        priceInPaise = priceInPaise + 1800;
    }

    static void tag(StringBuilder label) {
        label.append("-VIP");
    }

    static void reset(StringBuilder label) {
        label = new StringBuilder("RESET");
    }

    static void clean(String email) {
        email = email.strip().toLowerCase();
    }

    static String cleaned(String email) {
        return email.strip().toLowerCase();
    }

    static String kind(int x) {
        return "int";
    }

    static String kind(long x) {
        return "long";
    }

    static String kind(double x) {
        return "double";
    }

    public static void main(String[] args) {
        int price = 10_000;
        addTax(price);
        System.out.println(price);

        StringBuilder label = new StringBuilder("GOLD");
        tag(label);
        System.out.println(label);
        reset(label);
        System.out.println(label);

        String email = "  A@B.COM ";
        clean(email);
        System.out.println("[" + email + "]");
        System.out.println("[" + cleaned(email) + "]");

        System.out.println(kind(7) + " " + kind(7L) + " " + kind(7.0f) + " " + kind('x'));
    }
}
