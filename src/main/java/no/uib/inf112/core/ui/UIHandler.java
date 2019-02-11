package no.uib.inf112.core.ui;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import no.uib.inf112.core.RoboRally;
import no.uib.inf112.core.ui.event.ControlPanelEvent;
import no.uib.inf112.core.ui.event.events.CardClickedEvent;
import no.uib.inf112.core.ui.event.events.PowerDownEvent;

import java.io.File;


public class UIHandler implements Disposable {

    private final Skin skin;
    private final Table table;
    private final Stage stage;


    public static final String SKIN_NAME = "neutralizer-ui-skin";
    public static final String SKIN_FOLDER = "skins" + File.separator + SKIN_NAME + File.separatorChar;
    public static final String SKIN_JSON_FILE = SKIN_FOLDER + SKIN_NAME + ".json";

    private static final TextureRegion UI_BACKGROUND_TEXTURE;
    private static final TextureRegion CARDS_TEXTURE;

    private static final TextureRegion POWER_DOWN_TEXTURE;
    private static final TextureRegion LIFE_TOKEN_TEXTURE;
    private static final TextureRegion DAMAGE_TOKEN_TEXTURE;

    static {
        UI_BACKGROUND_TEXTURE = createTempRectTexture(1, 1, new Color(0.145f, 0.145f, 0.145f, 0.9f));
        CARDS_TEXTURE = createTempRectTexture(56, 90, Color.BLUE); //make sure the card are golden ratios (ish)
        POWER_DOWN_TEXTURE = createTempCircleTexture(41, Color.RED);
        LIFE_TOKEN_TEXTURE = createTempCircleTexture(25, Color.GREEN);
        DAMAGE_TOKEN_TEXTURE = createTempCircleTexture(19, Color.YELLOW);
    }

    /*
     * Size CANNOT be dividable by two, as it will make the returning texture look cut off
     */
    private static TextureRegion createTempCircleTexture(int size, Color color) {
        final Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getWidth() / 2, size / 2);
        return new TextureRegion(new Texture(pixmap));
    }

    private static TextureRegion createTempRectTexture(int width, int height, Color color) {
        final Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return new TextureRegion(new Texture(pixmap));
    }

    public UIHandler() {
        stage = new Stage(new ScreenViewport());
        RoboRally.getInputMultiplexer().addProcessor(stage);

//        stage.setDebugAll(true);

        skin = new Skin(Gdx.files.internal(SKIN_JSON_FILE));
        table = new Table(skin);

        create();
        resize();
    }

    /**
     * Initiate ui
     */
    private void create() {

        table.setBackground(new TextureRegionDrawable(UI_BACKGROUND_TEXTURE));
        table.padLeft(50);
        table.padRight(50);
        stage.addActor(table);
        table.setTransform(false);

        Table topRow = new Table(skin);
        table.add(topRow).expandX().fillX().align(Align.center);
        table.row();

        HorizontalGroup lifeTokens = new HorizontalGroup();
        topRow.add(lifeTokens).expandX().fillX().align(Align.bottomLeft);
        lifeTokens.space(5);
        for (int i = 0; i < 3; i++) {
            lifeTokens.addActor(new ImageButton(new TextureRegionDrawable(LIFE_TOKEN_TEXTURE)));
        }

        Container<ImageButton> power = new Container<>(createImgButton(PowerDownEvent.class, 0, POWER_DOWN_TEXTURE));
        topRow.add(power).fillX().align(Align.right);


        HorizontalGroup damageRow = new HorizontalGroup();
        table.add(damageRow).expandX().fillX().align(Align.left).padTop(5).padBottom(5);
        table.row();

        damageRow.space(5);
        for (int i = 0; i < 10; i++) {
            damageRow.addActor(new ImageButton(new TextureRegionDrawable(DAMAGE_TOKEN_TEXTURE)));
        }

        HorizontalGroup cardsRow = new HorizontalGroup();
        table.add(cardsRow).align(Align.left);
        cardsRow.space(5);
        for (int i = 0; i < 5; i++) {
            cardsRow.addActor(createImgButton(CardClickedEvent.class, i, CARDS_TEXTURE));
        }
    }

    private ImageButton createImgButton(Class<? extends ControlPanelEvent> eventType, int id,
                                        TextureRegion textureRegion) {
        ImageButton button = new ImageButton(new TextureRegionDrawable(textureRegion));
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ControlPanelEvent cpEvent;
                if (eventType == CardClickedEvent.class) {
                    cpEvent = new CardClickedEvent(id);
                }
                else {
                    cpEvent = new PowerDownEvent();
                }
                RoboRally.getControlPanelEventHandler().fireEvent(cpEvent);
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                button.getColor().a = 0.75f;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                button.getColor().a = 1;
            }
        });
        return button;
    }

    public void update() {
        stage.act(Gdx.graphics.getDeltaTime()); //Perform ui logic
        stage.draw(); //Draw the ui
    }

    public void resize() {
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        table.setHeight(table.getPrefHeight() + 15);

        table.setWidth(table.getPrefWidth());
        table.setX(Gdx.graphics.getWidth() / 2f - table.getPrefWidth() / 2);
        table.setY(5);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}

