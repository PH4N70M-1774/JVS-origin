package com.jvs.velox;

public class Utilities {
    /** Encode a signed 64-bit integer (Java long) to little-endian byte array. */
    public static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (value >> (8 * i)); // little-endian
        }
        return bytes;
    }

    /** Decode a signed 64-bit integer (Java long) from little-endian byte array. */
    public static long bytesToLong(byte[] bytes) {
        if (bytes.length != 8) {
            throw new IllegalArgumentException("Byte array must be exactly 8 bytes");
        }
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (bytes[i] & 0xFF)) << (8 * i);
        }
        return value;
    }
}
