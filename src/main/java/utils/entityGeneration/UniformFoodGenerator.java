package utils.entityGeneration;

import model.Field;
import model.Food;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author apomosov
 */
public class UniformFoodGenerator extends FoodGenerator {
    private final int threshold;
    private final double foodPerSecond;
    private final double removeChance;

    public UniformFoodGenerator(@NotNull Field field, double foodPerSecond, int threshold, double removeChance) {
        super(field);
        this.threshold = threshold;
        this.foodPerSecond = foodPerSecond;
        this.removeChance = removeChance;
        assert (removeChance >= 0 && removeChance <= 1);
    }

    @Override
    public void generate(@NotNull Duration elapsed) {
        //Remove or not?
        Random rand = new Random();
        if (rand.nextDouble() > 1 - removeChance) {
            List<Food> foods = new ArrayList<>(getField().getCells(Food.class));
            int toRemove = (int) (foods.size() * rand.nextDouble());
            for (int i = 0; i < toRemove; i++) {
                getField().removeCell(foods.get(i));
            }
        }
        if (getField().getCells(Food.class).size() <= threshold) {
            for (int i = 0; i < foodPerSecond; i++) {
                Food food = new Food(0, 0);
                food.setX(food.getRadius() + rand.nextInt(getField().getWidth() - 2 * food.getRadius()));
                food.setY(food.getRadius() + rand.nextInt(getField().getWidth() - 2 * food.getRadius()));
                getField().addCell(food);
            }
        }
    }
}
