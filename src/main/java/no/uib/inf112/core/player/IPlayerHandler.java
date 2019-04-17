package no.uib.inf112.core.player;

import no.uib.inf112.core.map.MapHandler;

import java.util.List;

/**
 * @author Daniel
 */
public interface IPlayerHandler {

    /**
     * Add all player cards in a queue
     * Execute cards for each player after priority
     */
    void endTurn();

    /**
     * Add all player to the round queue
     * Do turn for first player
     */
    void startTurn();

    /**
     * @return currently playing players
     */
    List<IPlayer> getPlayers();

    /**
     * @return the number of players playing
     */
    int getPlayerCount();

    /**
     * Move players to given spawning docks
     * Count number of flags in map
     *
     * @param map
     */
    void analyseMap(MapHandler map);

    /**
     *
     * @return number of flags to catch
     */
    int getFlagCount();
}


