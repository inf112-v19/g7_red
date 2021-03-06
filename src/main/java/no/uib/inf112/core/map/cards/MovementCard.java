package no.uib.inf112.core.map.cards;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.ui.UIHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MovementCard implements Card {

    private final Movement action;
    private final int priority;
    private TextureRegion textureRegion;

    /**
     * @param action   The movement the card will impose
     * @param priority The unique priority of the card
     */
    public MovementCard(@NotNull Movement action, int priority) {
        this.action = action;
        this.priority = priority;
        if (!GameGraphics.HEADLESS) {
            textureRegion = textureFromAction();
        }
    }

    private TextureRegion textureFromAction() {
        switch (action) {
            case MOVE_1:
                return UIHandler.MOVE1_TEXTURE;
            case MOVE_2:
                return UIHandler.MOVE2_TEXTURE;
            case MOVE_3:
                return UIHandler.MOVE3_TEXTURE;
            case BACK_UP:
                return UIHandler.BACK_UP_TEXTURE;
            case LEFT_TURN:
                return UIHandler.TURN_LEFT_TEXTURE;
            case RIGHT_TURN:
                return UIHandler.TURN_RIGHT_TEXTURE;
            case U_TURN:
                return UIHandler.U_TURN_TEXTURE;
            default:
                return null;
        }
    }


    /**
     * @return The action (movement) imposed by this card
     */
    @NotNull
    @Override
    public Movement getAction() {
        return action;
    }

    /**
     * @return The priority of this card
     */
    @Override
    public int getPriority() {
        return priority;
    }

    @NotNull
    @Override
    public TextureRegion getRegionTexture() {
        return textureRegion;
    }

    @NotNull
    @Override
    public String getTooltip() {
        return getAction().getTooltip() + "\nPriority: " + priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MovementCard that = (MovementCard) o;
        return priority == that.priority &&
                action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, priority);
    }

    @Override
    public String toString() {
        return "MovementCard{" +
                "action=" + action +
                ", priority=" + priority +
                '}';
    }

    @Override
    public int compareTo(@NotNull Card card) {
        return card.getPriority() - getPriority();
    }
}

