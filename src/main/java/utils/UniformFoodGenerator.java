package utils;

import model.Field;
import model.Food;
import model.GameConstants;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Random;

/**
 * @author apomosov
 */
public class UniformFoodGenerator extends FoodGenerator {
  private final int threshold;
  private final double foodPerSecond;

  public UniformFoodGenerator(@NotNull Field field, double foodPerSecond, int threshold) {
    super(field);
    this.threshold = threshold;
    this.foodPerSecond = foodPerSecond;
  }

  @Override
  public void generate(@NotNull Duration elapsed) {
    //Remove or not?
    Random rand = new Random();
    if (rand.nextDouble() > GameConstants.FOOD_REMOVE_CHANCE) {
      List<Food> foods = getField().getFoods();
      int toRemove = (int) (foods.size() * rand.nextDouble());
      for (int i = 0; i < toRemove; i++) {
        getField().removeFood(foods.get(i));
      }
    }
    if (getField().getFoods().size() <= threshold) {
      int toGenerate = (int) Math.ceil(foodPerSecond * elapsed.getSeconds());
      for (int i = 0; i < toGenerate; i++) {
        Food food = new Food(0, 0);
        food.setX(food.getRadius() + rand.nextInt(getField().getWidth() - 2 * food.getRadius()));
        food.setY(food.getRadius() + rand.nextInt(getField().getWidth() - 2 * food.getRadius()));
        getField().addFood(food);
      }
    }
  }
}
