package no.uib.inf112.core.player;

import com.badlogic.gdx.graphics.Color;
import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.map.MapHandler;
import no.uib.inf112.core.map.tile.tiles.SpawnTile;
import no.uib.inf112.core.screens.GameScreen;
import no.uib.inf112.core.util.ComparableTuple;
import no.uib.inf112.core.util.Direction;

import java.util.*;

import static no.uib.inf112.core.GameGraphics.HEADLESS;

public class PlayerHandler implements IPlayerHandler {

    private int playerCount;
    private int flagCount;
    private List<IPlayer> players;
    private Map<IPlayer, Long> wonPlayers;
    private Stack<ComparableTuple<String, Color>> colors;
    private IPlayer user;
    private boolean gameOver;
    private long startTime;

    /**
     * @param playerCount
     * @throws IllegalArgumentException if playerCount is invalid
     */
    public PlayerHandler(int playerCount, MapHandler map) {
        if (playerCount < 2) {
            if (!HEADLESS) {
                throw new IllegalArgumentException("Not enough players");
            }
        } else if (playerCount > 8) {
            throw new IllegalArgumentException("Too many players");
        }
        this.playerCount = playerCount;
        flagCount = 0;
        players = new ArrayList<>(playerCount);
        gameOver = false;
        startTime = System.currentTimeMillis();
        wonPlayers = new TreeMap<>();
        colors = new Stack<>();
        addColors();
        addPlayers(map);
    }

    private void addColors() {
        colors.push(new ComparableTuple<>("Pink", Color.PINK));
        colors.push(new ComparableTuple<>("Green", Color.GREEN));
        colors.push(new ComparableTuple<>("Purple", Color.PURPLE));
        colors.push(new ComparableTuple<>("Yellow", Color.YELLOW));
        colors.push(new ComparableTuple<>("Orange", Color.ORANGE));
        colors.push(new ComparableTuple<>("Cyan", Color.CYAN));
        colors.push(new ComparableTuple<>("Red", Color.RED));
        colors.push(new ComparableTuple<>("Blue", Color.BLUE));
    }

    @Override
    public void endTurn() {
        GameGraphics.getRoboRally().round();
    }

    @Override
    public void startTurn() {
        if (gameOver) {
            return;
        }
        GameScreen.getUiHandler().getPowerButton().resetAlpha();

        Player p = (Player) mainPlayer();
        p.setPoweredDown(p.willPowerDown());
        if (p.isDestroyed()) {
            return;
        }
        if (p.isPoweredDown()) {
            p.setWillPowerDown(false);
            p.endDrawCards();
        } else {
            p.beginDrawCards();
        }
    }

    @Override
    public List<IPlayer> getPlayers() {
        return players;
    }

    @Override
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Move players to given spawning docks
     * Count number of flags in map
     *
     * @param map
     */
    private void addPlayers(MapHandler map) {
        ComparableTuple<Integer, Stack<SpawnTile>> result = analyseMap(map);
        flagCount = result.key;
        Stack<SpawnTile> spawnTiles = result.value;
        if (!spawnTiles.empty()) {
            Collections.shuffle(spawnTiles);
            SpawnTile spawnTile = spawnTiles.pop();
            user = new Player(spawnTile.getX(), spawnTile.getY(), Direction.NORTH, map, new ComparableTuple<>(GameGraphics.mainPlayerName, Color.MAGENTA), 0);
            user.setDock(spawnTile.getSpawnNumber());
            players.add(user);
            while (spawnTiles.isEmpty() && players.size() < playerCount) {
                SpawnTile tile = spawnTiles.pop();
                StaticPlayer staticPlayer = new StaticPlayer(tile.getX(), tile.getY(), Direction.NORTH, map, colors.pop());
                staticPlayer.setDock(tile.getSpawnNumber());
                players.add(staticPlayer);
            }
        } else {
            for (int i = 0; i < playerCount; i++) {
                StaticPlayer staticPlayer = new StaticPlayer(i, 0, Direction.NORTH, map, colors.pop());
                staticPlayer.setDock(i);
                players.add(staticPlayer);
            }
        }
    }

    @Override
    public void checkGameOver() {
        players.removeIf(player -> {
            if (player.getFlags() == flagCount || player.isDestroyed()) {
                wonPlayers.put(player, System.currentTimeMillis());
                return true;
            }
            return false;
        });
        if (players.size() == 1) {
            wonPlayers.put(players.get(0), Math.abs(System.currentTimeMillis() - startTime));
            gameOver = true;
            return;
        }

        for (IPlayer player : players) {
            if (!player.isDestroyed()) {
                return;
            }
        }
        gameOver = true;
    }

    @Override
    public String[] rankPlayers() {
        //TODO Fix logic
//        if(players.size() == 1) {
//            players.remove(0);
//        }
        String[] playersInRankingOrder = new String[playerCount];
        List<IPlayer> playerStackWon = new ArrayList<>();
        playerStackWon.addAll(wonPlayers.keySet());
//        playerStackWon.addAll(players);
        playerStackWon.sort((p1, p2) -> {
            if (p1.getFlags() == p2.getFlags()) {
                if (p1.isDestroyed() && p2.isDestroyed()) {
                    return wonPlayers.get(p2).compareTo(wonPlayers.get(p1));
                } else {
                    return wonPlayers.get(p1).compareTo(wonPlayers.get(p2));
                }
            } else {
                return Integer.compare(p2.getFlags(), p1.getFlags());
            }
        });

        int i = 0;
        System.out.println(playerStackWon.size());
        for (IPlayer player : playerStackWon) {
            playersInRankingOrder[i++] = i + ". " + player.getName() + ": " + player.getFlags() + " flags";
        }
        return playersInRankingOrder;
    }

    @Override
    public Map<IPlayer, Long> getWonPlayers() {
        return wonPlayers;
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public IPlayer mainPlayer() {
        return players.get(0);
    }

    @Override
    public int getFlagCount() {
        return flagCount;
    }

    @Override
    public String toString() {
        return "PlayerHandler{" +
                "playerCount= " + playerCount +
                ", players= " + players +
                ", user= " + user +
                "}";
    }
}
