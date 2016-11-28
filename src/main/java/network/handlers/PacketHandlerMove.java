package network.handlers;

import main.ApplicationContext;
import messageSystem.Message;
import messageSystem.MessageSystem;
import messageSystem.messages.EjectMassMsg;
import messageSystem.messages.MoveMsg;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandMove;
import utils.JSONDeserializationException;
import utils.JSONHelper;

public class PacketHandlerMove implements PacketHandler {
  public void handle(@NotNull Session session, @NotNull String json) {
    CommandMove commandMove;
    try {
      commandMove = JSONHelper.fromJSON(json, CommandMove.class);
    } catch (JSONDeserializationException e) {
      e.printStackTrace();
    }

    log.info("Create MoveMsg");
    MessageSystem messageSystem = ApplicationContext.instance().get(MessageSystem.class);
    Message message = new MoveMsg();
    if (messageSystem == null) return;
    messageSystem.sendMessage(message);
  }
}
