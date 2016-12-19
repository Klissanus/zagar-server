package mechanics;

import model.*;
import org.jetbrains.annotations.NotNull;
import utils.EatComparator;

/**
 * Created by xakep666 on 18.12.16.
 */
public class CollisionHandler {

    private static EatComparator eatComparator = new EatComparator();

    private CollisionHandler() {}

    public static void handleCollision(@NotNull PlayerCell playerCell, @NotNull Cell cell) {
        //check actual collision
        if (playerCell.getCoordinate().distance(cell.getCoordinate()) >
                playerCell.getRadius() + cell.getRadius()) {
            return;
        }
        Field field = playerCell.getOwner().getField();
        if (cell instanceof Food) {
            playerCell.eat(cell);
            field.removeCell(cell);
        } else if (cell instanceof Virus) {

        } else if (cell instanceof PlayerCell) {
            int canEat = eatComparator.compare(playerCell,cell);
            if (canEat>0) {
                playerCell.eat(cell);
                field.removeCell(cell);
            } else if (canEat<0){
                ((PlayerCell) cell).eat(playerCell);
                field.removeCell(playerCell);
            }
        }
    }
}
