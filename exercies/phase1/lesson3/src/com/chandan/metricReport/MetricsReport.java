package com.chandan.metricReport;

public class MetricsReport {
    public static void main(String[] args) {
        int completedJobs = 45;
        int totalJobs = 60;
        double successRate = completedJobs / totalJobs * 100;

        int requestsPerDay = 86_400_000;
        int requestsPerMonth = requestsPerDay * 30;

        float avgLatencyMs = 12.5f;
        boolean hasErrors = true;

        long cacheTtlMs = 1000 * 60 * 60 * 24 * 60;
        int pendingJobs = totalJobs - completedJobs - 15;
        int requestsPerPendingJob = requestsPerDay / pendingJobs;

        System.out.println("Success rate: " + successRate + "%");
        System.out.println("Requests/month: " + requestsPerMonth);
        System.out.println("Avg latency: " + avgLatencyMs + " ms, errors: " + hasErrors);
        System.out.println("Cache TTL ms: " + cacheTtlMs);
        System.out.println("Requests per pending job: " + requestsPerPendingJob);
    }
}

// Before compiling: put every bug in one of four categories: compile time, launch time, run time, or silently wrong (it runs, but the output is wrong). The last category is new and the most dangerous.
// Compile and run it, fixing as you go. Watch out: fixing one kind of bug can reveal another kind.
// Write a fix for every bug, and say how you'd stop each silently-wrong bug from happening again.
