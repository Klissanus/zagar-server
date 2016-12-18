package model;

import java.time.Duration;

/**
 * @author apomosov
 */
public interface GameConstants {
    int TICKS_PER_SECOND = 20;
    int MAX_PLAYERS_IN_SESSION = 10;

    int FIELD_WIDTH = 1000;
    int FIELD_HEIGHT = 1000;

    int FOOD_MASS = 10;
    int DEFAULT_PLAYER_CELL_MASS = 40;
    int EJECTED_MASS = 30;
    int VIRUS_MASS = 100;

    double VIRUS_REMOVE_CHANCE = 0.3;
    int FOOD_PER_SECOND_GENERATION = 1;
    double FOOD_REMOVE_CHANCE = 0.3;

    int MASS_TO_EJECT = 3 * FOOD_MASS;
    int MASS_TO_SPLIT = 5 * DEFAULT_PLAYER_CELL_MASS;

    int MAX_FOOD_ON_FIELD = 100;
    int NUMBER_OF_VIRUSES = 10;

    Duration MOVEMENT_TIMEOUT = Duration.ofMinutes(2);
    double MAX_COORDINATE_DELTA_MODULE = 1.5;
    double INITIAL_SPEED = 500;
    double EJECTED_MASS_ACCELERATION = -50;
}
