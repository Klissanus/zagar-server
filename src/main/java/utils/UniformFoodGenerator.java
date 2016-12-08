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
public class UniformFoodGenerator implements FoodGenerator {
  @NotNull
  private final Field field;
  private final int threshold;
  private final double foodPerSecond;

  public UniformFoodGenerator(@NotNull Field field, double foodPerSecond, int threshold) {
    this.field = field;
    this.threshold = threshold;
    this.foodPerSecond = foodPerSecond;
  }

  @Override
  public void tick(@NotNull Duration elapsed) {
    //Remove or not?
    Random rand = new Random();
    if (rand.nextDouble() > GameConstants.FOOD_REMOVE_CHANCE) {
      List<Food> foods = field.getFoods();
      int toRemove = (int) (foods.size() * rand.nextDouble());
      for (int i = 0; i < toRemove; i++) {
        field.removeFood(foods.get(i));
      }
    }
    if (field.getFoods().size() <= threshold) {
      int toGenerate = (int) Math.ceil(foodPerSecond * elapsed.getSeconds());
      for (int i = 0; i < toGenerate; i++) {
        Food food = new Food(0, 0);
        food.setX(food.getRadius() + rand.nextInt(field.getWidth() - 2 * food.getRadius()));
        food.setY(food.getRadius() + rand.nextInt(field.getWidth() - 2 * food.getRadius()));
        field.addFood(food);
      }
    }
  }
}
