package no.uib.inf112.core.round.phase;

import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.map.MapHandler;
import no.uib.inf112.core.map.tile.Attribute;
import no.uib.inf112.core.map.tile.TileType;
import no.uib.inf112.core.map.tile.api.ConditionalRotateEffectTile;
import no.uib.inf112.core.map.tile.api.MovableTile;
import no.uib.inf112.core.map.tile.api.Tile;
import no.uib.inf112.core.map.tile.tiles.ConveyorTile;
import no.uib.inf112.core.util.Vector2Int;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The phase where CONVEYOR move the {@link MovableTile}. If a CONVEYOR has the {@link Attribute#HIGH_PRIORITY} it is considered
 * an express CONVEYOR.
 * <p>
 * In reality it this phase is two phases. First one where only the express CONVEYOR moves, then one where every CONVEYOR moves.
 *
 * @author Elg
 */
public class ConveyorPhase extends AbstractPhase {

    public ConveyorPhase(long totalRunTime) {
        super(totalRunTime);
    }

    //TODO ISSUE #118 test, first all express CONVEYOR moves, then **all** conveyors move
    @Override
    public void startPhase(@NotNull MapHandler map) {
        subPhase(map, false, 0);
        //the next sub-phase must be after a bit of delay for it to properly work
        GameGraphics.scheduleSync(() -> subPhase(map, true, getRunTime() / 4), getRunTime() / 2);
    }

    private void subPhase(MapHandler map, boolean allConveyors, long delayForRotation) {
        Map<MovableTile, Vector2Int> movedTiles = new HashMap<>();
        for (int x = 0; x < map.getMapWidth(); x++) {
            for (int y = 0; y < map.getMapHeight(); y++) {
                Tile tile = map.getTile(MapHandler.BOARD_LAYER_NAME, x, y);


                //get a tile of the correct type
                if (tile != null &&
                        (tile.getTileType() == TileType.CONVEYOR || tile.getTileType() == TileType.ROTATION_CONVEYOR) &&
                        (allConveyors || tile.hasAttribute(Attribute.HIGH_PRIORITY))) {
                    ConveyorTile conveyor = (ConveyorTile) tile;
                    List<Tile> tiles = map.getAllTiles(x, y);
                    for (Tile otherTile : tiles) {
                        if (conveyor.canDoAction(otherTile)) {
                            movedTiles.put((MovableTile) otherTile, new Vector2Int(otherTile.getX(), otherTile.getY()));
                            if (conveyor.action((MovableTile) otherTile)) {
                                conveyor.getActionSound().play();
                            } else {
                                movedTiles.remove(otherTile);
                            }
                        }
                    }
                }
            }
        }
        GameGraphics.scheduleAsync(() -> rotateMoved(map, movedTiles), delayForRotation);
    }

    private void rotateMoved(MapHandler map, Map<MovableTile, Vector2Int> movedTiles) {
        for (Map.Entry<MovableTile, Vector2Int> entry : movedTiles.entrySet()) {
            if (entry.getKey().getX() != entry.getValue().x || entry.getKey().getY() != entry.getValue().y) {
                //Entity moved
                Tile tile = map.getTile(MapHandler.BOARD_LAYER_NAME, entry.getKey().getX(), entry.getKey().getY());
                if (tile.getTileType() == TileType.ROTATION_CONVEYOR) {
                    ((ConditionalRotateEffectTile) tile).rotate(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
