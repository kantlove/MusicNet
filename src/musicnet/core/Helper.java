package musicnet.core;

import musicnet.model.SearchResult;
import musicnet.model.SongFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
            double score = DiceCoefficient.percentMatch(query, f.getName());
            if (score >= threshold || f.getName().toLowerCase().contains(query.toLowerCase().trim())) {
                results.add(new SearchResult(f, score));
            }
        }

        Collections.sort(results, (o1, o2) -> Double.compare(o2.score, o1.score));
        return results;
    }

    public static String getFileCheckSum(File file) throws IOException, NoSuchAlgorithmException {
        // use MD5 algorithm
        MessageDigest digest = MessageDigest.getInstance("MD5");

        // get file input stream for reading the file content
        FileInputStream stream = new FileInputStream(file);

        // create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount;

        // read file data and update in message digest
        while ((bytesCount = stream.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        // close the stream; We don't need it now.
        stream.close();

        // get the hash's bytes
        byte[] bytes = digest.digest();

        // this bytes[] has bytes in decimal format;
        // convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        // return complete hash
        return sb.toString();
    }

    public static String getDataCheckSum(byte[] data) throws NoSuchAlgorithmException {
        // use MD5 algorithm
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update(data);

        // get the hash's bytes
        byte[] bytes = digest.digest();

        // this bytes[] has bytes in decimal format;
        // convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        // return complete hash
        return sb.toString();
    }

    public static void copyFile(File sourceFile, File destFile) {
        try {
            if (!sourceFile.exists()) return;
            if (!destFile.exists()) {
                if (!destFile.createNewFile()) return;
            }
            FileChannel source = new FileInputStream(sourceFile).getChannel();
            FileChannel destination = new FileOutputStream(destFile).getChannel();
            if (source != null) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
