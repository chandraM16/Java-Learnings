package com.chandan.delivery;

public class DeliveryStats {

    private  static int FAST = 0, NORMAL = 1, LATE = 2, INVALID = 3;

    private static int[] countDeliveryStatus(String[] timings) {
        int[] statusCount = new int[4];
        for (String timeString : timings) {
            int time = Integer.parseInt(timeString);
            if (time < 0) {
                statusCount[INVALID]++;
            } else if (time >= 45) {
                statusCount[LATE]++;
            } else if (30 <= time && time < 45) {
                statusCount[NORMAL]++;
            } else if ( time < 30) {
                statusCount[FAST]++;
            }
        }

        return statusCount;
    }

    private static int fastestDelivery(String[] timings) {
        int fastDel = Integer.MAX_VALUE;
        for (String timeString : timings) {
            int time = Integer.parseInt(timeString);
            if (time > -1 && time < fastDel) {
                fastDel = time;
            }

        }
        return fastDel;
    }

    private static int slowestDelivery(String[] timings) {
        int slowDel = Integer.MIN_VALUE;
        for (String timeString : timings) {
            int time = Integer.parseInt(timeString);
            if (time > -1 && time > slowDel) {
                slowDel = time;
            }

        }
        return slowDel;
    }

    private static int validTimeCount(int[] statusCount) {
        return statusCount[FAST] + statusCount[NORMAL] + statusCount[LATE];
    }

    private static double avgTiming(String[] timings, int[] statusCount) {
        int totalTime = 0;
        for (String timeString : timings) {
            int time = Integer.parseInt(timeString);
            if (time > -1)
                totalTime += time;
        }
        int countOfValidTimes = validTimeCount(statusCount);
        double avgTime = (double) totalTime / countOfValidTimes;
        return avgTime;

    }

    private static double latePercentage(int[] statusCount) {
        int countOfValidTimes = validTimeCount(statusCount);
        int lateTimeCount = statusCount[LATE];
        return (double) lateTimeCount / countOfValidTimes * 100;

    }

    private static String summary(int validCount, int invalidCount, int fastDelCount, int normalDelCount,
            int lateDelCount, double avgDelTime, int fastestDel, int slowestDel, double latePercent) {
        return """
                Deliveries: %d valid, %d invalid
                Fast: %d, Normal: %d, Late: %d
                Average: %.2f min, fastest: %d min, slowest: %d min
                Late: %.2f%%
                            """.formatted(validCount, invalidCount, fastDelCount, normalDelCount, lateDelCount,
                avgDelTime, fastestDel, slowestDel, latePercent);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: DeliveryStats <minutes>");
            return;
        }


        int[] statusCount = countDeliveryStatus(args);
        if(statusCount[INVALID] == args.length){
            System.out.println("Deliveries: %d valid, %d invalid".formatted(0, statusCount[INVALID])+ " No valid deliveries");
            return;
        }
        int fastestDel = fastestDelivery(args);
        int slowestDel = slowestDelivery(args);
        int validCount = validTimeCount(statusCount);
        int invalidCount = statusCount[INVALID];
        double avgTime = avgTiming(args, statusCount);
        double latePercent = latePercentage(statusCount);
        String sumString = summary(validCount, invalidCount, statusCount[FAST], statusCount[NORMAL], statusCount[LATE], avgTime, fastestDel, slowestDel, latePercent);

        System.out.println(sumString);

    }
}
