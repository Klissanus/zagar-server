package replication;

import main.ApplicationContext;
import matchmaker.MatchMaker;
import model.*;
import network.ClientConnections;
import network.packets.PacketReplicate;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xakep666 on 16.11.16.
 * <p>
 * Replicates only cells which fits in player window
 * Window size stored in {@link Player} object
 */
public class InsideWindowReplicator implements Replicator {
    private static final int widthDelta = 10;
    private static final int heightDelta = 10;
    private static final double widthFactor = 1;
    private static final double heightFactor = 1;

    @Override
    public void replicate() {
        for (GameSession gameSession : ApplicationContext.instance().get(MatchMaker.class).getActiveGameSessions()) {
            gameSession.getPlayers().forEach(player -> {
                List<PlayerCell> playerCells = player.getCells();
                Point2D center = calculateCenter(playerCells);
                int windowWidth = player.getWindowWidth();
                int windowHeight = player.getWindowHeight();

                final Rectangle2D border = new Rectangle2D.Double(
                        center.getX() - (windowWidth * widthFactor / 2) - widthDelta,
                        center.getY() + (windowHeight * heightFactor / 2) + heightDelta,
                        center.getX() + (windowWidth * widthFactor / 2) + widthDelta,
                        center.getY() - (windowHeight * heightFactor / 2) - heightDelta);

                List<PlayerCell> playerCellsToSend = playerCells.stream()
                        .filter(cell -> border.contains(cell.getCoordinate()))
                        .collect(Collectors.toList());
                List<Food> foodsToSend = gameSession.getField()
                        .getCells(Food.class)
                        .stream()
                        .filter(cell -> border.contains(cell.getCoordinate()))
                        .collect(Collectors.toList());
                List<Virus> virusesToSend = gameSession.getField()
                        .getCells(Virus.class)
                        .stream()
                        .filter(cell -> border.contains(cell.getCoordinate()))
                        .collect(Collectors.toList());
                sendToPlayer(player, playerCellsToSend, foodsToSend, virusesToSend);
            });
        }
    }

    @NotNull
    private Point2D calculateCenter(@NotNull List<PlayerCell> cells) {
        double x = 0;
        double y = 0;
        for (Cell cell : cells) {
            x += cell.getCoordinate().getX();
            y += cell.getCoordinate().getY();
        }
        x /= cells.size();
        y /= cells.size();
        return new Point2D.Double(x, y);
    }

    private void sendToPlayer(@NotNull Player player,
                              @NotNull List<PlayerCell> playerCells,
                              @NotNull List<Food> foods,
                              @NotNull List<Virus> viruses) {
        Session session = ApplicationContext.instance().get(ClientConnections.class).getSessionByPlayer(player);
        if (session == null) return;
        List<protocol.model.Cell> playerCellsToSend = playerCells.stream()
                .map(cell -> new protocol.model.Cell(
                        cell.getId(),
                        player.getId(),
                        false,
                        cell.getRadius(),
                        (int)cell.getCoordinate().getX(),
                        (int)cell.getCoordinate().getY()))
                .collect(Collectors.toList());

        List<protocol.model.Food> foodsToSend = foods.stream()
                .map(f -> new protocol.model.Food((int)f.getCoordinate().getX(), (int)f.getCoordinate().getY()))
                .collect(Collectors.toList());
        playerCellsToSend.addAll(
                viruses.stream()
                        .map(virus ->
                                //negative IDs shows that cell not belongs to player
                                new protocol.model.Cell(
                                        -1,
                                        -1,
                                        true,
                                        virus.getMass(),
                                        (int)virus.getCoordinate().getX(),
                                        (int)virus.getCoordinate().getY()))
                        .collect(Collectors.toList())
        );
        try {
            new PacketReplicate(playerCellsToSend, foodsToSend).write(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
