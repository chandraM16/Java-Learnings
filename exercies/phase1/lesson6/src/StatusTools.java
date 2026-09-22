public class StatusTools {
    static String normalize(String status) {
        return status.strip().toLowerCase();
    }

    static double  percentage(int part, int total) {
        return (double) part / total * 100;
    }

    static String riskLevel(double failedPercent) {
        if (failedPercent >= 50) {
            return "HIGH";
        } else if (failedPercent >= 20) {
            return "MEDIUM";
        }
        return "LOW";
    }

    public static void main(String[] args) {

        if(args.length == 0){
            System.out.println("Please provide some Args");
            return;
        }
        String status = args.length > 0 ? args[0] : null;
        status = normalize(status);

        int failed = 3;
        int total = 7;
        double failedPercent = percentage(failed, total);

        System.out.println("Status: [" + status + "]");
        System.out.println("Failed: " + String.format("%.2f", failedPercent) + "%, risk: " + riskLevel(failedPercent));
    }
}