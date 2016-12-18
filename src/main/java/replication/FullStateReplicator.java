package replication;

import main.ApplicationContext;
import matchmaker.MatchMaker;
import model.GameSession;
import network.ClientConnections;
import network.packets.PacketReplicate;
import protocol.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alpi
 * @since 31.10.16
 *
 * Replicates full session state to clients
 */
public class FullStateReplicator implements Replicator {
    @Override
    public void replicate() {
        for (GameSession gameSession : ApplicationContext.instance().get(MatchMaker.class).getActiveGameSessions()) {
            List<Cell> replicateCells = gameSession.getField().getAllCells().stream()
                    .map(cell->{
                        if (cell instanceof model.PlayerCell) {
                            model.PlayerCell c = ((model.PlayerCell) cell);
                            return new PlayerCell(
                                    c.getId(),c.getRadius(),c.getCoordinate(),c.getOwner().getUser().getName()
                            );
                        }
                        if (cell instanceof model.EjectedMass) {
                            model.EjectedMass c = ((model.EjectedMass) cell);
                            return new EjectedMass(c.getRadius(),c.getCoordinate());
                        }
                        if (cell instanceof model.Food) {
                            model.Food c = ((model.Food) cell);
                            return new Food(c.getRadius(),c.getCoordinate());
                        }
                        if (cell instanceof model.Virus) {
                            model.Virus c = ((model.Virus) cell);
                            return new Virus(c.getRadius(),c.getCoordinate());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            ApplicationContext.instance().get(ClientConnections.class).getConnections().forEach(connection -> {
                if (gameSession.getPlayers().contains(connection.getKey())
                        && connection.getValue().isOpen()) {
                    try {
                        new PacketReplicate(replicateCells).write(connection.getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
