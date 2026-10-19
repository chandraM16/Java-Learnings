import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AccessLogReport {
    static final class LogLine {
        private final String apiMethod;
        private final String apiEndPoint;
        private final int apiStatusCode;
        private final String apiUser;

        public LogLine(String apiStatus, String apiEndPoint, int apiStatusCode, String apiUser) {
            this.apiMethod = apiStatus.toUpperCase();
            this.apiEndPoint = apiEndPoint;
            this.apiStatusCode = apiStatusCode;
            this.apiUser = apiUser.toLowerCase();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LogLine l = (LogLine) o;
            return l.apiEndPoint.equals(apiEndPoint) && l.apiMethod.equals(apiMethod) && l.apiUser.equals(apiUser)
                    && apiStatusCode == l.apiStatusCode;
        }

        @Override
        public int hashCode() {
            return Objects.hash(apiMethod, apiEndPoint, apiStatusCode, apiUser);
        }

        @Override
        public String toString() {
            return apiMethod + " /" + apiEndPoint + " " + apiStatusCode + " " + apiUser;
        }

    }

    private final List<LogLine> logs = new ArrayList<>();

    private void addLog(LogLine log) {
        logs.add(log);
    }

    private static final List<String> RAW = List.of(
            "POST /orders 201 alice",
            "GET  /orders 200 alice",
            "GET  /users  403 bob",
            "POST /orders 500 carol",
            "GET  /orders 200 bob",
            "POST /orders 201 alice");

    private LogLine parse(String line) {
        String[] parts = line.split("\\s+");
        return new LogLine(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]);
    }

    private void fillLogs() {
        for (String string : RAW) {
            addLog(parse(string));
        }
    }

    private void printStatusCount() {
        Map<Integer, Integer> statusCount = new HashMap<>();

        for (LogLine logLine : logs) {
            statusCount.put(logLine.apiStatusCode, statusCount.getOrDefault(logLine.apiStatusCode, 0) + 1);
        }
        List<Integer> codes = new ArrayList<>(statusCount.keySet());

        Collections.sort(codes);

        System.out.println("status counts:");

        for (int code : codes) {
            System.out.println("  " + code + " -> " + statusCount.get(code));
        }

    }

    private void printUniqueUsers() {
        Set<String> uniqueUsers = new HashSet<>();
        for (LogLine logLine : logs) {
            uniqueUsers.add(logLine.apiUser);
        }

        System.out.println("unique users: " + uniqueUsers.size());
    }

    private void printUsersByStatus() {
        Map<Integer, Set<String>> usersByStatus = new HashMap<>();
        for (LogLine logLine : logs) {
            usersByStatus.computeIfAbsent(logLine.apiStatusCode, k -> new LinkedHashSet<>())
                    .add(logLine.apiUser);

        }
        List<Integer> codes = new ArrayList<>(usersByStatus.keySet());
        Collections.sort(codes);

        System.out.println("users by status:");
        
        for (int code : codes) {
            System.out.println("  " + code + " -> " + usersByStatus.get(code).toString());
        }

    }

    private void printUserReqCount(String userName) {
        int count = 0;

        for (LogLine logLine : logs) {
            if (logLine.apiUser.equals(userName.toLowerCase())) {
                count++;
            }
        }
        System.out.println(userName + " requests: " + count);

    }

    public static void main(String[] args) {
        AccessLogReport accReport = new AccessLogReport();
        accReport.fillLogs();
        accReport.printStatusCount();
        accReport.printUniqueUsers();
        accReport.printUsersByStatus();
        accReport.printUserReqCount("alice");
    }
}
