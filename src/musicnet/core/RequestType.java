package musicnet.core;

/**
 * Created by mt on 12/2/2015.
 */
public enum RequestType {
    GetHosts, // get the list of known hosts from other peer
    SendHosts, //  send the list of known hosts to a peer
    Search,
    GetFilesList,
    SendFilesList,
    GetFile, // get file
    SendFile
}
