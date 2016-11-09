package model;

import java.time.Duration;

/**
 * @author apomosov
 */
public interface GameConstants {
  int MAX_PLAYERS_IN_SESSION = 10;
  int FIELD_WIDTH = 1000;
  int FIELD_HEIGHT = 1000;
  int FOOD_MASS = 10;
  int DEFAULT_PLAYER_CELL_MASS = 40;
  int VIRUS_MASS = 100;
  double VIRUS_GENERATION_CHANCE = 0.3;
  int FOOD_PER_SECOND_GENERATION = 1;
  double FOOD_REMOVE_CHANCE = 0.3;
  int MAX_FOOD_ON_FIELD = 100;
  int NUMBER_OF_VIRUSES = 10;
  Duration MOVEMENT_TIMEOUT = Duration.ofMinutes(2);
}
