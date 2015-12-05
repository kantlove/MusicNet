package musicnet.core;

/**
 * Created by mt on 12/5/2015.
 */
public final class Config {
    public static final int CHUNK_SIZE = 8192;
    public static final int RECEIVE_BASKET_SIZE = CHUNK_SIZE * 8; // must be always much larger than CHUNK_SIZE
}
