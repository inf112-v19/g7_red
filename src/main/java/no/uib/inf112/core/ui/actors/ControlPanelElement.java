package no.uib.inf112.core.ui.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Class to hide boilerplate code when creating control panel elements (such as life and health)
 *
 * @author Elg
 */
public abstract class ControlPanelElement extends ImageButton implements DisabledVisualizer {

    public ControlPanelElement(TextureRegion textureRegion) {
        super(new TextureRegionDrawable(textureRegion));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        act();
    }
}
