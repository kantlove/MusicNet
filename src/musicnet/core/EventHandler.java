package musicnet.core;

import java.util.List;

@FunctionalInterface
public interface EventHandler {
    /**
     * Invoke event handler
     * @param sender Event caller
     * @param arg Event arguments
     */
    void invoke(Object sender, Object arg);
}
