package network.handlers;

import main.ApplicationContext;
import messageSystem.Message;
import messageSystem.MessageSystem;
import messageSystem.messages.EjectMassMsg;
import messageSystem.messages.SplitMsg;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandSplit;
import utils.JSONDeserializationException;
import utils.JSONHelper;

public class PacketHandlerSplit implements PacketHandler {
  public void handle(@NotNull Session session, @NotNull String json) {
    CommandSplit commandSplit;
    try {
      commandSplit = JSONHelper.fromJSON(json, CommandSplit.class);
    } catch (JSONDeserializationException e) {
      e.printStackTrace();
      return;
    }

    log.info("Create SplitMsg");
    MessageSystem messageSystem = ApplicationContext.instance().get(MessageSystem.class);
    Message message = new SplitMsg();
    if (messageSystem == null) return;
    messageSystem.sendMessage(message);
  }
}
