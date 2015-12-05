package musicnet.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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

    public static byte[] mergeChunks(List<DataChunk> chunks) {
        int length = 0;
        for (DataChunk c : chunks) {
            if(c == null) {
                Console.log("Missing part " + chunks.indexOf(c));
                continue; // packet loss or something, ignore
            }
            length += c.data.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (DataChunk c : chunks) {
            if(c == null)
                continue;
            for (byte element : c.data) {
                result[pos] = element;
                pos++;
            }
        }
        return result;
    }

    public static <T>int countNonNull(Collection<T> list) {
        int count = 0;
        for(T e : list) {
            count += (e != null) ? 1 : 0;
        }
        return count;
    }

    public static List<File> getFilesInDirectory(String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        List<File> results = new ArrayList<>();

        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    results.add(listOfFiles[i]);
                }
            }
        }
        return results;
    }
}
