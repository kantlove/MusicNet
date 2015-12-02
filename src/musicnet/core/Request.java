package musicnet.core;

/**
 * Created by mt on 12/2/2015.
 */
public class Request {
    public RequestType type;
    public String[] params; // [0]: ip address, [1]: port, [2...]: custom params
}
