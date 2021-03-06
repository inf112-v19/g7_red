package no.uib.inf112.core.map.tile.tiles;

import no.uib.inf112.core.map.tile.Attribute;
import no.uib.inf112.core.map.tile.TileGraphic;
import no.uib.inf112.core.map.tile.api.AbstractRequirementTile;
import no.uib.inf112.core.map.tile.api.ActionTile;
import no.uib.inf112.core.map.tile.api.SingleDirectionalTile;
import no.uib.inf112.core.map.tile.api.Tile;
import no.uib.inf112.core.ui.Sound;
import no.uib.inf112.core.util.Direction;
import no.uib.inf112.core.util.Vector2Int;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Elg
 */
public class GearTile extends AbstractRequirementTile implements ActionTile<SingleDirectionalTile> {

    private Attribute dir;

    public GearTile(@NotNull Vector2Int pos, @NotNull TileGraphic tg) {
        super(pos, tg);

        if (hasAttribute(Attribute.RIGHT)) {
            dir = Attribute.RIGHT;
        }
        if (hasAttribute(Attribute.LEFT)) {
            if (dir != null) {
                throw new IllegalStateException("Gear " + tg.name() + " rotates both to the right and to the left");
            }
            dir = Attribute.LEFT;
        }
        if (dir == null) {
            throw new IllegalStateException("Gear " + tg.name() + " does not rotate");
        }

    }

    @Override
    public boolean action(@NotNull SingleDirectionalTile tile) {
        Direction orgRotation = tile.getDirection();
        Direction newDirection;
        switch (dir) {
            case RIGHT:
                newDirection = orgRotation.turnRight();
                break;
            case LEFT:
                newDirection = orgRotation.turnLeft();
                break;
            default:
                throw new IllegalArgumentException("Unknown direction " + dir);
        }
        return tile.setDirection(newDirection);
    }

    @NotNull
    @Override
    public Sound getActionSound() {
        return Sound.CONVEYOR;
    }

    @Nullable
    @Override
    public List<Class<? extends Tile>> requiredSuperClasses() {
        return Collections.singletonList(SingleDirectionalTile.class);
    }

    @Override
    public String toString() {
        return "GearTile{" + "dir=" + dir + '}';
    }
}
