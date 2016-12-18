package messageSystem.messages;

import mechanics.Mechanics;
import messageSystem.Abonent;
import messageSystem.Message;
import model.EjectedMass;
import model.GameConstants;
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
        log.trace("Eject mass msg created");

    }

    @Override
    public void exec(Abonent abonent) {
        log.trace("EjectMassMsg exec() call");
        player.getCells().forEach(cell-> {
            EjectedMass ejectedMass = new EjectedMass(
                    cell.getCoordinate(),
                    cell.getLastMovement(),
                    GameConstants.EJECTED_MASS,
                    GameConstants.INITIAL_SPEED,
                    GameConstants.EJECTED_MASS_ACCELERATION);
            cell.getOwner().getField().addCell(ejectedMass);
        });
    }
}
