package utils;

import model.Field;
import model.GameConstants;
import model.Virus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * @author apomosov
 */
public class RandomVirusGenerator implements VirusGenerator {
  @NotNull
  private final Field field;
  private final int numberOfViruses;


  public RandomVirusGenerator(@NotNull Field field, int numberOfViruses) {
    this.field = field;
    this.numberOfViruses = numberOfViruses;
  }

  @Override
  public void generate() {
    Random random = new Random();
    //Generate  or not?
    if (random.nextDouble() > GameConstants.VIRUS_GENERATION_CHANCE) return;

    List<Virus> onField = field.getViruses();

    int virusesToRemove = (int) (onField.size() * random.nextDouble());
    for (int i = 0; i < virusesToRemove; i++) {
      field.removeVirus(onField.get(i));
    }

    int virusRadius = (int) Math.sqrt(GameConstants.VIRUS_MASS / Math.PI);
    for (int i = 0; i < numberOfViruses - virusesToRemove; i++) {
      Virus virus = new Virus(
          virusRadius + random.nextInt(field.getWidth() - 2 * virusRadius),
          virusRadius + random.nextInt(field.getHeight() - 2 * virusRadius)
      );
      field.addVirus(virus);
    }
  }
}
