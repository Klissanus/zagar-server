package mechanics;

import model.Cell;
import model.Food;
import model.PlayerCell;
import model.Virus;
import org.jetbrains.annotations.NotNull;
import utils.EatComparator;

/**
 * Created by xakep666 on 18.12.16.
 */
public class CollisionHandler {
    private CollisionHandler() {}

    public static void handleCollision(@NotNull PlayerCell playerCell, @NotNull Cell cell) {
        //check actual collision
        if (playerCell.getCoordinate().distance(cell.getCoordinate()) >
                playerCell.getRadius() + cell.getRadius()) return;
        EatComparator eatComparator = new EatComparator();
        if (cell instanceof Food) {
            playerCell.eat(cell);
        } else if (cell instanceof Virus) {

        } else if (cell instanceof PlayerCell) {
            int canEat = eatComparator.compare(playerCell,cell);
            if (canEat>0) {
                playerCell.eat(cell);
                playerCell.getOwner().getField().removeCell(cell);
            } else if (canEat<0){
                ((PlayerCell) cell).eat(playerCell);
                playerCell.getOwner().getField().removeCell(playerCell);
            }
        }
    }
}
