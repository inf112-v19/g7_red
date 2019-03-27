package no.uib.inf112.core.map.tile.tiles;

import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.map.tile.TileGraphic;
import no.uib.inf112.core.map.tile.api.AbstractRequirementTile;
import no.uib.inf112.core.map.tile.api.ActionTile;
import no.uib.inf112.core.map.tile.api.BackupableTile;
import no.uib.inf112.core.map.tile.api.Tile;
import no.uib.inf112.core.util.Vector2Int;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * @author Elg
 */
public class WrenchTile extends AbstractRequirementTile implements ActionTile<BackupableTile> {

    public WrenchTile(@NotNull Vector2Int pos, @NotNull TileGraphic tg) {
        super(pos, tg);
    }

    @Override
    public void action(@NotNull BackupableTile tile) {
        tile.getBackup().x = getX();
        tile.getBackup().y = getY();
        System.out.println("Taking backup of " + tile + " with " + getClass().getSimpleName());
    }

    @Override
    public void playActionSound() {
        GameGraphics.getSoundPlayer().playRobotUpdatesBackup();
    }

    @Nullable
    @Override
    public List<Class<? extends Tile>> requiredSuperClasses() {
        return Collections.singletonList(BackupableTile.class);
    }
}