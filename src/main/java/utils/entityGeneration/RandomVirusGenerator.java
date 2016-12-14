package utils.entityGeneration;

import model.Field;
import model.GameConstants;
import model.Virus;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author apomosov
 */
public class RandomVirusGenerator extends VirusGenerator {
  private final int numberOfViruses;

  public RandomVirusGenerator(@NotNull Field field, int numberOfViruses) {
    super(field);
    this.numberOfViruses = numberOfViruses;
  }

  @Override
  public void generate(@NotNull Duration elapsed) {
    Random random = new Random();
    //Generate  or not?
    if (random.nextDouble() > GameConstants.VIRUS_GENERATION_CHANCE) return;

    List<Virus> onField = new ArrayList<>(getField().getCells(Virus.class));

    int virusesToRemove = (int) (onField.size() * random.nextDouble());
    for (int i = 0; i < virusesToRemove; i++) {
      getField().removeCell(onField.get(i));
    }

    int virusRadius = (int) Math.sqrt(GameConstants.VIRUS_MASS / Math.PI);
    for (int i = 0; i < numberOfViruses - virusesToRemove; i++) {
      Virus virus = new Virus(
              virusRadius + random.nextInt(getField().getWidth() - 2 * virusRadius),
              virusRadius + random.nextInt(getField().getHeight() - 2 * virusRadius)
      );
      getField().addCell(virus);
    }
  }
}
