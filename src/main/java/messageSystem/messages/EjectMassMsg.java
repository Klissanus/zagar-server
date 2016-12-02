package messageSystem.messages;

import mechanics.Mechanics;
import messageSystem.Abonent;
import messageSystem.Message;
import model.Player;
import network.ClientConnectionServer;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandEjectMass;

/**
 * Created by Klissan on 28.11.2016.
 */
public class EjectMassMsg extends Message {

    @NotNull
    private CommandEjectMass command;
    @NotNull
    private Player player;

    public EjectMassMsg(@NotNull Player player, @NotNull CommandEjectMass command) {
        super(Message.getMessageSystem().getService(ClientConnectionServer.class).getAddress(),
                Message.getMessageSystem().getService(Mechanics.class).getAddress());
        this.command = command;
        this.player = player;
        log.info("Eject mass msg created");

    }

    @Override
    public void exec(Abonent abonent) {
        log.info("EjectMassMsg exec() call");
        Message.getMessageSystem().getService(Mechanics.class).ejectMass(player, command);
    }
}
