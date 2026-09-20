package com.chandan.auth;

public class AccessCheck {
    public static void main(String[] args) {
        String username = "guest";
        String role = "user";

        if (args.length > 1) {
            username = args[0];
            role = args[1];
        } else if (args.length > 0) {
            username = args[0];
        }

        boolean isAdmin = "admin".equalsIgnoreCase(role);

        String email = username + "@acme.io";
        int loginCount = 7;
        int failedLogins = 2;
        double failureRate = (double)failedLogins / loginCount * 100;
        long sessionTimeoutMs = 1000L * 60 * 60 * 24 * 30;

        System.out.println("User: " + email);
        System.out.println("Admin? " + isAdmin);
        System.out.println("Failure rate: " + failureRate + "%");
        System.out.println("Session timeout ms: " + sessionTimeoutMs);
    }
}
