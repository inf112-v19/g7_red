package no.uib.inf112.core.player;

import com.badlogic.gdx.Gdx;
import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.map.OutSideBoardException;
import no.uib.inf112.core.ui.CardContainer;
import no.uib.inf112.core.ui.actors.cards.SlotType;
import no.uib.inf112.core.ui.event.ControlPanelEventHandler;
import no.uib.inf112.core.ui.event.ControlPanelEventListener;
import no.uib.inf112.core.ui.event.events.PowerDownEvent;
import no.uib.inf112.core.util.Vector2Int;
import no.uib.inf112.desktop.Main;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author Elg
 */
public class Player {

    public static final int MAX_LIVES = 3;
    public static final int MAX_HEALTH = 10;
    public static final int MAX_PLAYER_CARDS = 5;
    public static final int MAX_DRAW_CARDS = MAX_HEALTH - 1;

    private Robot robot;

    private Vector2Int backup;

    private int dock;

    private int lives;
    private boolean poweredDown;
    private int health;

    private boolean headless;

    private CardContainer cards;

    /**
     * @param x         Start x position
     * @param y         Start y position
     * @param direction Start direction
     * @throws IllegalArgumentException See {@link Robot#Robot(int, int, Direction, boolean)}
     * @throws IllegalStateException    See {@link Robot#Robot(int, int, Direction, boolean)}
     */
    public Player(int x, int y, @NotNull Direction direction) {
        this(x, y, direction, false);
    }

    /**
     * @param x         Start x position
     * @param y         Start y position
     * @param direction Start direction
     * @param headless  true if you want player without graphics, false otherwise
     * @throws IllegalArgumentException See {@link Robot#Robot(int, int, Direction, boolean)}
     * @throws IllegalStateException    See {@link Robot#Robot(int, int, Direction, boolean)}
     */
    public Player(int x, int y, @NotNull Direction direction, boolean headless) {
        backup = new Vector2Int(x, y);

        lives = MAX_LIVES;
        health = MAX_HEALTH;
        poweredDown = false;
        this.headless = headless;
        robot = new Robot(x, y, direction, headless);
        cards = new CardContainer(this);
        if (!Main.HEADLESS) {
            ControlPanelEventHandler eventHandler = GameGraphics.getCPEventHandler();
            eventHandler.registerListener(PowerDownEvent.class, (ControlPanelEventListener<PowerDownEvent>) event -> {
                if (this != GameGraphics.getRoboRally().getPlayerHandler().mainPlayer()) {
                    //This is not optimal, references both ways but since its a get i have not tried to change it
                    return;

                }
                poweredDown = !poweredDown;
                System.out.println("Powered down? " + isPoweredDown());
            });
        }
    }

    /**
     * damage the player by the given amount and handles death if health is less than or equal to 0
     *
     * @param damageAmount How much to damage the player
     * @throws IllegalArgumentException If the damage amount is not positive
     */
    public void damage(int damageAmount) {
        if (damageAmount <= 0) {
            throw new IllegalArgumentException("Cannot do non-positive damage");
        }
        health -= damageAmount;
        if (health <= 0) {
            kill();
        }
    }

    /**
     * Kill the player, decreasing their lives and depending on Main.headless permanently remove from map if there are
     * not lives left
     */
    public void kill() {
        lives--;
        if (lives == 0) {
            GameGraphics.getRoboRally().getCurrentMap().removeEntity(robot);
            return;
        }
        health = MAX_HEALTH;
        robot.teleport(backup.x, backup.y);
    }

    /**
     * Heal the player by the given amount up to {@link #MAX_HEALTH}
     *
     * @param healAmount How much to heal
     * @throws IllegalArgumentException If the heal amount is not positive
     */
    public void heal(int healAmount) {
        if (healAmount <= 0) {
            throw new IllegalArgumentException("Cannot do non-positive damage");
        }
        health = Math.min(MAX_HEALTH, health + healAmount);
    }

    /**
     * @return If the player is dead. A player is dead if their lives are 0 or less
     */
    public boolean isDestroyed() {
        return lives <= 0;
    }

    @NotNull
    public CardContainer getCards() {
        return cards;
    }

    public void beginDrawCards() {
        cards.draw();
        GameGraphics.getUiHandler().showDrawnCards();
    }

    public void endDrawCards() {

        GameGraphics.getUiHandler().hideDrawnCards();

        if (cards.hasInvalidHand()) {
            cards.randomizeHand();
        }

        for (int i = 0; i < Player.MAX_PLAYER_CARDS; i++) {
            int id = i;

            //this is a way to do player turns (ie wait some between each card is played)
            GameGraphics.executorService.schedule(() -> Gdx.app.postRunnable(() -> {
                Card card = cards.getCard(SlotType.HAND, id);
                moveRobot(card);

            }), 500 * (i + 1), TimeUnit.MILLISECONDS);
        }
    }

    public void moveRobot(Card card) {
        try {
            getRobot().move(card.getAction());
        } catch (OutSideBoardException outSideBoardException) {
            kill();
        }
    }

    public int getLives() {
        return lives;
    }

    public int getHealth() {
        return health;
    }

    public boolean isPoweredDown() {
        return poweredDown;
    }

    public int getDamageTokens() {
        return MAX_HEALTH - health;
    }

    public Vector2Int getBackup() {
        return backup;
    }

    public void setBackup(int x, int y) {
        backup.x = x;
        backup.y = y;
    }

    public Robot getRobot() {
        return robot;
    }

    public int getDock() {
        return dock;
    }

    public void setDock(int dock) {
        this.dock = dock;
    }
}
