package dev.neylz.benchmarked.util;

public class IntCharEncoder {

    public static String encode(long value) {
        //encode onto [0,9], [a,z] and [A,Z] (62 characters) -> %62
        StringBuilder builder = new StringBuilder();

        while (value > 0) {
            long remainder = value % 62;
            value /= 62;
            builder.append(encodeChar(remainder));
        }

        return builder.reverse().toString();

    }

    private static char encodeChar(long remainder) {
        if (remainder < 10) {
            return (char) ('0' + remainder);
        } else if (remainder < 36) {
            return (char) ('a' + remainder - 10);
        } else {
            return (char) ('A' + remainder - 36);
        }
    }

    public static long decode(String encoded) {
        long value = 0;
        for (long i = 0; i < encoded.length(); i++) {
            value *= 62;
            value += decodeChar(encoded.charAt((int) i));
        }
        return value;
    }

    private static long decodeChar(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a' + 10;
        } else {
            return c - 'A' + 36;
        }
    }
}
