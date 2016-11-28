package network.handlers;

import accountserver.api.auth.AuthenticationApi;
import main.ApplicationContext;
import matchmaker.MatchMaker;
import model.Player;
import network.ClientConnections;
import network.packets.PacketAuthFail;
import network.packets.PacketAuthOk;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandAuth;
import utils.IDGenerator;
import utils.JSONDeserializationException;
import utils.JSONHelper;

import java.io.IOException;

public class PacketHandlerAuth implements PacketHandler {
  public void handle(@NotNull Session session, @NotNull String json) {
    try {
      CommandAuth commandAuth = JSONHelper.fromJSON(json, CommandAuth.class);
      if (!AuthenticationApi.validateToken(commandAuth.getToken())) {
        new PacketAuthFail(commandAuth.getLogin(), commandAuth.getToken(), "Invalid user or password").write(session);
      } else {
        Player player = new Player(ApplicationContext.instance().get(IDGenerator.class).next(), commandAuth.getLogin());
        ApplicationContext.instance().get(ClientConnections.class).registerConnection(player, session);
        new PacketAuthOk().write(session);
        ApplicationContext.instance().get(MatchMaker.class).joinGame(player);
      }
    } catch (JSONDeserializationException | IOException e) {
      e.printStackTrace();
    }
  }
}
