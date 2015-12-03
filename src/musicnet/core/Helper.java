package musicnet.core;

import java.util.List;

/**
 * Created by mt on 12/2/2015.
 */
public final class Helper {
    /**
     * Insert an item to a list at arbitrary index. Override any existing item.
     */
    public static <T> void insert(List<T> list, T item, int index) {
        while(list.size() <= index) {
            list.add(null);
        }
        list.set(index, item);
    }

    /**
     * Convert a primitive array to a wrapper array
     */
    public static Byte[] toObject(byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new Byte[0];
        }
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    /**
     * Convert a wrapper array to a primitive array
     */
    public static byte[] toPrimitive(Byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new byte[0];
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i];
        }
        return result;
    }

    public static byte[] mergeChunks(List<Byte[]> chunks) {
        int length = 0;
        for (Byte[] c : chunks) {
            length += c.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (Byte[] array : chunks) {
            for (byte element : array) {
                result[pos] = element;
                pos++;
            }
        }
        return result;
    }
}
