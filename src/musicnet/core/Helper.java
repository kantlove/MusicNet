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
}
