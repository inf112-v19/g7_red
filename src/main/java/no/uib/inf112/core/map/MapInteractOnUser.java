package no.uib.inf112.core.map;

import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.map.MapAction.MapAction;
import no.uib.inf112.core.player.Entity;
import no.uib.inf112.core.player.IPlayer;
import no.uib.inf112.core.util.Vector2Int;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapInteractOnUser {

    /**
     * Scan the entitites and the tiles they are standing on and try to do the actions on the map
     *
     * @param entitiesOnMap all the  entities on the map
     * @return true
     */
    public boolean scan(Collection<Entity> entitiesOnMap) {
        ArrayList<IPlayer> players = GameGraphics.getRoboRally().getPlayerHandler().getPlayers();

        findAndDoMovement(entitiesOnMap);

        shootLasers(entitiesOnMap);

        registerSpecialTiles(players);
        return true;
    }

    /**
     * Finds all entites standing on tiles with no mapmovement(e.g. flags, wrenches)
     *
     * @param players A list of all the players
     */
    private void registerSpecialTiles(@NotNull ArrayList<IPlayer> players) {
        for (IPlayer player : players) {
            //Looping through players instead of entities because players can register flags and entity doesn't have a reference to player
            int x = player.getRobot().getX();
            int y = player.getRobot().getY();
            TileType tileUnderRobot;
            tileUnderRobot = GameGraphics.getRoboRally().getCurrentMap().getFlagLayerTile(x, y);
            if (tileUnderRobot == null) {
                return;
            }
            switch (tileUnderRobot.getGroup()) {
                case FLAG:
                    switch (tileUnderRobot) {
                        case FLAG1:
                            if (player.canGetFlag(1)) {
                                registerFlag(player, x, y);
                                GameGraphics.getSoundPlayer().playFlag();
                            }
                            break;
                        case FLAG2:
                            if (player.canGetFlag(2)) {
                                registerFlag(player, x, y);
                                GameGraphics.getSoundPlayer().playFlag();
                            }
                            break;
                        case FLAG3:
                            if (player.canGetFlag(3)) {
                                registerFlag(player, x, y);
                                GameGraphics.getSoundPlayer().playFlag();
                            }
                            break;
                        case FLAG4:
                            if (player.canGetFlag(4)) {
                                registerFlag(player, x, y);
                                GameGraphics.getSoundPlayer().playFlag();
                            }
                            break;
                    }
                case OPTION:
                    switch (tileUnderRobot) {
                        case WRENCH:
                            player.setBackup(x, y);
                            GameGraphics.getSoundPlayer().playRobotUpdatesBackup();

                        case HAMMER_AND_WRENCH:
                            player.setBackup(x, y);
                            GameGraphics.getSoundPlayer().playGetOptionCard();
                            //TODO #25 get option card? (Should do more than what happens on normal wrench)
                    }
                default:
                    break; //Case if the tile is not a special tile (flag or option)
            }
        }
    }


    /**
     * Method to register a flag visit for a player and set it's backup to the location of flag
     *
     * @param player The player that should register flag
     * @param x      x coordinate of where the flag and player are standing
     * @param y      y coordinate of where the flag and player are standing
     */
    private void registerFlag(IPlayer player, int x, int y) {
        player.registerFlagVisit();
        player.setBackup(x, y);
    }


    /**
     * Finds all entities in line of a laser, (this should also shoot lasers from robots).
     *
     * @param entitiesOnMap
     */
    private void shootLasers(Collection<Entity> entitiesOnMap) {
        //Need additional logic since only robot closest to laser start is hit, also shoot lasers from robots

        ArrayList<MapAction> queue = getMapActions(entitiesOnMap);
        for (MapAction mapAction : queue) {
            mapAction.doAction();
        }
    }


    private ArrayList<MapAction> getMapActions(Collection<Entity> entitiesOnMap) {
        ArrayList<MapAction> queue = new ArrayList<>(); //Not a queue but using it as a queue
        for (Entity entity : entitiesOnMap) {
            MapAction mapAction = getAction(entity);
            if (mapAction != null) {//And mapaction is laser
                queue.add(mapAction); // Add every action thats needed
            }
        }
        return queue;
    }

    /**
     * finds all enitites standing on coneyors and does logic to move those that should move
     *
     * @param entitiesOnMap
     */
    private void findAndDoMovement(Collection<Entity> entitiesOnMap) {
        Map<Vector2Int, Entity> posRobotMap = new HashMap<>(entitiesOnMap.size());
        for (Entity entity : entitiesOnMap) {
            posRobotMap.put(new Vector2Int(entity.getX(), entity.getY()), entity);
        } //Keep track of old positions

        ArrayList<MapAction> queue = getMapActions(entitiesOnMap);

        Map<Entity, Entity> entityConflictWithEntity = new HashMap<>(); //Currently not used just stores information
        ArrayList<MapAction> conflictActions = new ArrayList<>();
        for (MapAction mapAction : queue) {
            if (posRobotMap.containsKey(mapAction.getResultOfMovement())) {
                entityConflictWithEntity.put(mapAction.getParent(), posRobotMap.get(mapAction.getResultOfMovement()));
                conflictActions.add(mapAction);
                queue.remove(mapAction);
            } else {
                posRobotMap.put(mapAction.getResultOfMovement(), mapAction.getParent());
                posRobotMap.remove(new Vector2Int(mapAction.getParent().getX(), mapAction.getParent().getY()));
            }
        }

        for (MapAction mapAction : conflictActions) {
            if (posRobotMap.containsKey(mapAction.getResultOfMovement())) {
                Entity conflictingRobot = posRobotMap.get(mapAction.getResultOfMovement());
                if (new Vector2Int(conflictingRobot.getX(), conflictingRobot.getY()) == mapAction.getResultOfMovement()) {
                    //do nothing movement is not allowed on stationary other robot
                } else {
                    // Revert conflicting robot nobody moves!
                    for (MapAction mapAction1 : queue) {
                        if (mapAction1.getParent() == conflictingRobot) {
                            queue.remove(mapAction1);
                            Vector2Int pos = new Vector2Int(conflictingRobot.getX(), conflictingRobot.getY());
                            if (!posRobotMap.containsKey(pos)) { // No robot on this position
                                posRobotMap.remove(mapAction1.getResultOfMovement());
                                posRobotMap.put(pos, conflictingRobot);
                            } else {
                                // This is dangerous, for what if a robot moved to this position?
                                // Handle that someone moved to this position, recursive function call?
                            }

                        }
                    }
                }
            } else {
                //conflict solved itself
                queue.add(mapAction);
            }
        }

        for (MapAction mapAction : queue) {
            mapAction.doAction();
        }
    }

    /**
     * Returns a mapAction object according to the tile the player is standing on
     *
     * @param entity entity standing on a tile
     * @return a mapAction if standing on a tile that should have corresponding action, null otherwise
     */
    @Nullable
    private MapAction getAction(@NotNull Entity entity) {
        //Switch on tile
        //TODO check for holes, issue #72
        return null;
    }
}
