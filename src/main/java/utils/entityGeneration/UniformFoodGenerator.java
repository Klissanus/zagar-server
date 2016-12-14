package utils.entityGeneration;

import model.Field;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

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
    /*Random rand = new Random();
    if (rand.nextDouble() > GameConstants.FOOD_REMOVE_CHANCE) {
      List<Food> foods = new ArrayList<>(getField().getCells(Food.class));
      int toRemove = (int) (foods.size() * rand.nextDouble());
      for (int i = 0; i < toRemove; i++) {
        getField().removeCell(foods.get(i));
      }
    }
    if (getField().getCells(Food.class).size() <= threshold) {
      int toGenerate = (int) Math.ceil(foodPerSecond * elapsed.getSeconds());
      for (int i = 0; i < toGenerate; i++) {
        Food food = new Food(0, 0);
        food.setX(food.getRadius() + rand.nextInt(getField().getWidth() - 2 * food.getRadius()));
        food.setY(food.getRadius() + rand.nextInt(getField().getWidth() - 2 * food.getRadius()));
        getField().addCell(food);
      }
    }*/
  }
}
