package network.handlers;

import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

/**
 * Created by xakep666 on 28.11.16.
 *
 * Interface for packet handlers
 */
public interface PacketHandler {
    void handle(@NotNull Session session, @NotNull String message);
}
