package replication;

import main.ApplicationContext;
import matchmaker.MatchMaker;
import model.*;
import network.ClientConnections;
import network.packets.PacketReplicate;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import utils.entitySearching.InsideAreaFinder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by xakep666 on 16.11.16.
 * <p>
 * Replicates only cells which fits in player window
 */
public class InsideWindowReplicator implements Replicator {
    private static final int widthDelta = 10;
    private static final int heightDelta = 10;
    private static final double widthFactor = 1;
    private static final double heightFactor = 1;

    private static int checked(@NotNull Function<Integer, Boolean> clause, int value, int valueIfFalse) {
        return clause.apply(value) ? value : valueIfFalse;
    }

    @Override
    public void replicate() {
        for (GameSession gameSession : ApplicationContext.instance().get(MatchMaker.class).getActiveGameSessions()) {
            gameSession.getPlayers().forEach(player -> {
                Point center = calculateCenter(player);
                int windowWidth = player.getWindowWidth();
                int windowHeight = player.getWindowHeight();

                final int leftBorder = checked(value -> value > 0,
                        center.x - (int) (windowWidth * widthFactor / 2) - widthDelta, 0);
                final int rightBorder = checked(value -> value < GameConstants.FIELD_WIDTH,
                        center.x + (int) (windowWidth * widthFactor / 2) + widthDelta, GameConstants.FIELD_HEIGHT);

                final int topBorder = checked(value -> value < GameConstants.FIELD_HEIGHT,
                        center.y + (int) (windowHeight * heightFactor / 2) + heightDelta, GameConstants.FIELD_HEIGHT);
                final int bottomBorder = checked(value -> value > 0,
                        center.y - (int) (windowHeight * heightFactor / 2) - heightDelta, 0);

                InsideAreaFinder finder = ApplicationContext.instance().get(InsideAreaFinder.class);

                List<PlayerCell> playerCells = new LinkedList<>();
                gameSession.getPlayers().forEach(p ->
                        playerCells.addAll(finder.findInArea(p.getCells(),
                                leftBorder, rightBorder, bottomBorder, topBorder)));
                List<Food> foods = finder.findInArea(gameSession.getField().getCells(model.Food.class),
                        leftBorder, rightBorder, bottomBorder, topBorder);
                List<Virus> viruses = finder.findInArea(gameSession.getField().getCells(model.Virus.class),
                        leftBorder, rightBorder, bottomBorder, topBorder);

                sendToPlayer(player, playerCells, foods, viruses);
            });
        }
    }

    private Point calculateCenter(Player player) {
        int x = 0;
        int y = 0;
        List<PlayerCell> cells = player.getCells();
        for (Cell cell : cells) {
            x += cell.getX();
            y += cell.getY();
        }
        x /= cells.size();
        y /= cells.size();
        return new Point(x, y);
    }

    private void sendToPlayer(@NotNull Player player,
                              @NotNull List<PlayerCell> playerCells,
                              @NotNull List<Food> foods,
                              @NotNull List<Virus> viruses) {
        Session session = ApplicationContext.instance().get(ClientConnections.class).getSessionByPlayer(player);
        if (session == null) return;
        List<protocol.model.Cell> playerCellsToSend = playerCells.stream()
                .map(cell -> new protocol.model.Cell(cell.getId(), player.getId(),
                        false, cell.getRadius(), cell.getX(), cell.getY()))
                .collect(Collectors.toList());

        List<protocol.model.Food> foodsToSend = foods.stream()
                .map(f -> new protocol.model.Food(f.getX(), f.getY()))
                .collect(Collectors.toList());
        playerCellsToSend.addAll(
                viruses.stream()
                        .map(virus ->
                                //negative IDs shows that cell not belongs to player
                                new protocol.model.Cell(-1, -1, true, virus.getMass(), virus.getX(), virus.getY()))
                        .collect(Collectors.toList())
        );
        try {
            new PacketReplicate(playerCellsToSend, foodsToSend).write(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
