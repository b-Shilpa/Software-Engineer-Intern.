package io.cdap.wrangler.api.parser;

public class TimeDuration extends Token {
    private long nanoseconds;

    public TimeDuration(String token) {
        // Parse the token and convert to nanoseconds
        // Example: "150ms" -> 150000000
        // Implement parsing logic here
    }

    public long getNanoseconds() {
        return nanoseconds;
    }
}
