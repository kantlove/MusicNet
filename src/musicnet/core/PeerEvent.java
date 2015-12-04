package musicnet.core;

import java.util.*;

/**
 * Created by mt on 12/4/2015.
 */
public class PeerEvent {
    protected Set<EventHandler> listeners = new HashSet<>();

    public PeerEvent(){}

    protected <T>void invoke(Object sender, T arg) {
        for(EventHandler handler : listeners) {
            handler.invoke(sender, arg);
        }
    }

    public void subscribe(EventHandler handler) {
        if(!listeners.contains(handler))
            listeners.add(handler);
    }
}
