package no.uib.inf112.core.player;

import no.uib.inf112.core.map.cards.Card;
import no.uib.inf112.core.map.cards.Movement;
import no.uib.inf112.core.util.ComparableTuple;
import org.jetbrains.annotations.NotNull;

public interface IPlayer extends Comparable<IPlayer>, Entity {

    /**
     * Max number of lives a player can have
     */
    int MAX_LIVES = 3;
    /**
     * Max number of health a player can have
     */
    int MAX_HEALTH = 10;
    /**
     * Max number of cards a player can draw.
     */
    int MAX_DRAW_CARDS = MAX_HEALTH - 1;
    /**
     * Max number of cards on the control panel
     */
    int MAX_PLAYER_CARDS = 5;

    /**
     * Remove all lives
     * and remove from map
     */
    void destroy();

    /**
     * @return If the player is dead. A player is dead if their lives are 0 or less
     */
    boolean isDestroyed();

    /**
     * @param id
     * @return the next player card in queue
     */
    ComparableTuple<Card, IPlayer> getNextCard(int id);

    /**
     * Moves the robot with the movement corresponding to the cardAction
     * <p>
     * If the robot moves outside the map, {@link AbstractPlayer#kill()} method is called
     *
     * @param cardAction movement to do with the robot
     */
    void move(Movement cardAction, int maxTime);

    default void move(Movement cardAction) {
        move(cardAction, 0);
    }

    /**
     * @return lives
     */
    int getLives();

    /**
     * @return health
     */
    int getHealth();

    /**
     * Changes name
     *
     * @param name new name
     */
    void setName(String name);

    /**
     * @return name of player
     */
    String getName();

    /**
     * Returns the id of this player
     * <p>
     * Computer controlled players return -1
     *
     * @return the id of this player
     */
    int getId();

    /**
     * @return if powered down or not
     */
    boolean isPoweredDown();

    /**
     * @return if robot will power down next round
     */
    boolean willPowerDown();

    /**
     * @return amount of damage tokens
     */
    int getDamageTokens();

    /**
     * Sets the backup for the player
     *
     * @param x The new x coordinate of the backup
     * @param y The new y coordinate of the backup
     * @throws IllegalArgumentException if {@code x} or {@code y} is outside the current map
     */
    void setBackup(int x, int y);

    /**
     * @return assigned dock
     */
    int getDock();

    /**
     * @param dock
     */
    void setDock(int dock);

    /**
     * @return Amount of flags visited
     */
    int getFlags();

    /**
     * @param flagNr The flags number
     * @return If player can get given FLAG or not
     */
    boolean canGetFlag(int flagNr);

    /**
     * Update number of flags visited by one
     */
    void registerFlagVisit();

    /**
     * Sets the powered down state for this player
     *
     * @param poweredDown true if this player should be powering down
     */
    void setPoweredDown(boolean poweredDown);

    /**
     * Set if the player in the future will power down
     *
     * @param poweredDown true if the player will power down
     */
    void setWillPowerDown(boolean poweredDown);

    @Override
    int compareTo(@NotNull IPlayer o);
}
