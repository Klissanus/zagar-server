package network.handlers;

import main.ApplicationContext;
import messageSystem.Message;
import messageSystem.MessageSystem;
import messageSystem.messages.EjectMassMsg;
import messageSystem.messages.ReplicateMsg;
import network.packets.PacketAuthFail;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandEjectMass;
import utils.JSONDeserializationException;
import utils.JSONHelper;

import java.io.IOException;

public class PacketHandlerEjectMass implements PacketHandler {
  public void handle(@NotNull Session session, @NotNull String json) {
    CommandEjectMass commandEjectMass;
    try {
      commandEjectMass = JSONHelper.fromJSON(json, CommandEjectMass.class);
    } catch (JSONDeserializationException e) {
      e.printStackTrace();
    }

    log.info("Create EjectMassMsg");
    MessageSystem messageSystem = ApplicationContext.instance().get(MessageSystem.class);
    Message message = new EjectMassMsg();
    if (messageSystem == null) return;
    messageSystem.sendMessage(message);
  }
}
