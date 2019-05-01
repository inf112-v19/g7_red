package no.uib.inf112.core.player;

import no.uib.inf112.core.map.MapHandler;
import no.uib.inf112.core.map.tile.TileType;
import no.uib.inf112.core.map.tile.api.Tile;
import no.uib.inf112.core.map.tile.tiles.SpawnTile;
import no.uib.inf112.core.util.ComparableTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
    default ComparableTuple<Integer, Stack<SpawnTile>> analyseMap(MapHandler map) {
        Stack<SpawnTile> spawnTiles = new Stack<>();
        int flagCount = 0;
        for (int x = 0; x < map.getMapWidth(); x++) {
            for (int y = 0; y < map.getMapHeight(); y++) {
                Tile boardTile = map.getTile(MapHandler.BOARD_LAYER_NAME, x, y);
                Tile flagTile = map.getTile(MapHandler.FLAG_LAYER_NAME, x, y);

                if (boardTile != null && boardTile.getTileType() == TileType.SPAWN) {
                    SpawnTile spawnTile = (SpawnTile) boardTile;
                    spawnTiles.add(spawnTile);
                }

                if (flagTile != null && flagTile.getTileType() == TileType.FLAG) {
                    flagCount++;
                }
            }
        }
        return new ComparableTuple<Integer, Stack<SpawnTile>>(flagCount, spawnTiles);
    }

    /**
     * Checks if game is over
     * Updates game over field variable
     */
    void checkGameOver();

    default void removePlayers() {
        getPlayers().removeIf(player -> {
            if (player.getFlags() == getFlagCount() || player.isDestroyed()) {
                getWonPlayers().put(player, System.currentTimeMillis());
                return true;
            }
            return false;
        });
    }

    /**
     * Rank players according to flags and
     * time played
     *
     * @return String list of players ranked in correct order
     */
    default String[] rankPlayers() {
        getPlayers().forEach(player -> getWonPlayers().put(player, System.currentTimeMillis()));
        List<IPlayer> playerStackWon = new ArrayList<>(getWonPlayers().keySet());
        playerStackWon.sort((p1, p2) -> {
            if (p1.getFlags() == p2.getFlags()) {
                if (p1.isDestroyed() && !p2.isDestroyed()) {
                    return 1;
                } else if (p2.isDestroyed() && !p1.isDestroyed()) {
                    return -1;
                } else if (p1.isDestroyed() && p2.isDestroyed()) {
                    return getWonPlayers().get(p2).compareTo(getWonPlayers().get(p1));
                } else {
                    return getWonPlayers().get(p1).compareTo(getWonPlayers().get(p2));
                }
            } else {
                return Integer.compare(p2.getFlags(), p1.getFlags());
            }
        });

        String[] playersInRankingOrder = new String[getPlayerCount()];
        int i = 0;
        for (IPlayer player : playerStackWon) {
            playersInRankingOrder[i++] = i + ". " + player.getName() + ": " + player.getFlags() + " flags";
        }
        return playersInRankingOrder;
    }

    /**
     * @return number of flags to catch
     */
    int getFlagCount();

    IPlayer mainPlayer();

    boolean isGameOver();

    Map<IPlayer, Long> getWonPlayers();
}

