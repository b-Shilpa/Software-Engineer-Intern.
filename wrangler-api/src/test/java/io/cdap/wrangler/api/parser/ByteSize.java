package io.cdap.wrangler.api.parser;

public class ByteSize extends Token {
    private long bytes;

    public ByteSize(String token) {
        // Parse the token and convert to bytes
        // Example: "10KB" -> 10240 bytes
        // Implement parsing logic here
    }

    public long getBytes() {
        return bytes;
    }
}
