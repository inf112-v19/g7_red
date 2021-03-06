package no.uib.inf112.core.player;

import com.badlogic.gdx.graphics.Color;
import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.map.MapHandler;
import no.uib.inf112.core.util.ComparableTuple;
import no.uib.inf112.core.util.Direction;
import no.uib.inf112.core.util.Vector2Int;
import no.uib.inf112.desktop.TestGraphics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegisterBackupTest extends TestGraphics {


    private MapHandler map = GameGraphics.getRoboRally().getCurrentMap();

    @Test
    public void settingBackupShouldUpdateBackup() {
        AbstractPlayer testPlayer = new NonPlayer(1, 1, Direction.NORTH, map, new ComparableTuple<>("Black", Color.BLACK));
        Vector2Int newBackup = new Vector2Int(0, 0);
        testPlayer.setBackup(newBackup.x, newBackup.y);
        assertEquals(newBackup, testPlayer.getBackup());
    }

}
