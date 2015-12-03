package musicnet.core;

import java.io.Serializable;

/**
 * Created by mt on 12/2/2015.
 */
public class DataChunk implements Serializable {
    public int sequence = 0; // sequence number
    public int total; // total number of chunks of this file
    public String id; // same id = same file
    public Datatype type = Datatype.Object;
    public Byte[] data;

    public DataChunk(String id, byte[] data) {
        this(0, id, data);
    }

    public DataChunk(int sequence, String id, byte[] data) {
        this(sequence, 1, id, data);
    }

    public DataChunk(int sequence, int total, String id, byte[] data) {
        this.sequence = sequence;
        this.total = total;
        this.id = id;
        this.data = Helper.toObject(data);
    }
}
