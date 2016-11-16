package replication;

import main.ApplicationContext;
import matchmaker.MatchMaker;
import model.*;

import java.util.List;

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

    @Override
    public void replicate() {
        for (GameSession gameSession : ApplicationContext.instance().get(MatchMaker.class).getActiveGameSessions()) {
            gameSession.getPlayers().forEach(player -> {
                Point center = calculateCenter(player);
                int windowWidth = player.getWindowWidth();
                int windowHeight = player.getWindowHeight();

                int leftBorder = center.x - (int) (windowWidth * widthFactor / 2) - widthDelta;
                if (leftBorder < 0) leftBorder = 0;
                int rightBorder = center.x + (int) (windowWidth * widthFactor / 2) + widthDelta;
                if (rightBorder > GameConstants.FIELD_WIDTH) rightBorder = GameConstants.FIELD_WIDTH;

                int topBorder = center.y + (int) (windowHeight * heightFactor / 2) + heightDelta;
                if (topBorder > GameConstants.FIELD_HEIGHT) topBorder = GameConstants.FIELD_HEIGHT;
                int bottomBorder = center.y - (int) (windowHeight * heightFactor / 2) - heightDelta;
                if (bottomBorder < 0) bottomBorder = 0;

                //TODO search cells on field in calculated borders
                //TODO send replication packets
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

    private static final class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
