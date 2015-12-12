package musicnet.core;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by mt on 12/2/2015.
 */
public final class Helper {
    /**
     * Insert an item to a list at arbitrary index. Override any existing item.
     */
    public static <T> void insert(List<T> list, T item, int index) {
        while (list.size() <= index) {
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
            if (c == null) {
                Console.log("Missing part " + chunks.indexOf(c));
                continue; // packet loss or something, ignore
            }
            length += c.data.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (DataChunk c : chunks) {
            if (c == null)
                continue;
            for (byte element : c.data) {
                result[pos] = element;
                pos++;
            }
        }
        return result;
    }

    public static <T> int countNonNull(Collection<T> list) {
        int count = 0;
        for (T e : list) {
            count += (e != null) ? 1 : 0;
        }
        return count;
    }

    public static List<SongFile> getFilesInDirectory(String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        List<SongFile> results = new ArrayList<>();

        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    results.add(new SongFile(listOfFiles[i]));
                }
            }
        }
        return results;
    }

    public static Type getElementType(List<?> list) {
        assert list.size() > 0;
        return list.get(0).getClass();
    }

    /**
     * Find a list of songs base on matching between query and song names
     *
     * @param query     query that contains the name of the target song
     * @param files     list of available songs to search
     * @param threshold songs that has matching result lower than threshold will be discarded
     * @return
     */
    public static List<SearchResult> bulkMatch(String query, List<SongFile> files, double threshold) {
        List<SearchResult> results = new ArrayList<>();
        for (SongFile f : files) {
            double score = DiceCoefficient.percentMatch(query, f.name);
            if (score >= threshold) {
                results.add(new SearchResult(f, score));
            }
        }

        Collections.sort(results, new Comparator<SearchResult>() {
            @Override
            public int compare(SearchResult o1, SearchResult o2) {
                return Double.compare(o2.score, o1.score);
            }
        });
        return results;
    }
}
