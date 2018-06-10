package edu.salleurl.g6.gc;

public class OffsetFactory {

    private static int offset;

    public static void reset() {
        offset = 0;
    }

    public static int nextOffset(int size) {
        int current_offset = offset;
        offset += size;
        return current_offset;
    }

}
