package no.uib.inf112.core.screens;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import no.uib.inf112.core.GameGraphics;

import java.io.File;

public class SetupScreen extends AbstractMenuScreen {


    private String[] MAP_LIST = new String[]{"Risky Exchange", "Checkmate", "Dizzy Dash", "Island Hop", "Chop Shop Challenge"};
    private Drawable mapImg;
    private final Drawable SELECT_BOX_BACKGROUND = new TextureRegionDrawable(new Texture("drop_down_background.png"));
    private BitmapFont listFont;
    private BitmapFont selectedFont;

    private static final String MAP_IMG_FOLDER = GameGraphics.MAP_FOLDER + "mapImages" + File.separatorChar;
    private static final String MAP_IMG_EXTENSION = ".png";


    public SetupScreen(GameGraphics game) {
        super(game);

        //MAP_LIST = Gdx.files.internal(MAP_IMG_FOLDER.toString())

        mapImg = new TextureRegionDrawable(new Texture(MAP_IMG_FOLDER + GameGraphics.mapFileName + MAP_IMG_EXTENSION));
        listFont = game.generateFont("screen_font.ttf", 20);
        selectedFont = game.generateFont("screen_font_bold.ttf", 25);
    }

    @Override
    public void show() {
        stage.addActor(createReturnButton());
        stage.addActor(createMapSelectBox());

    }

    @Override
    public void render(float v) {
        super.render(v);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        mapImg.draw(game.batch, camera.viewportWidth / 4f, camera.viewportHeight / 6f, camera.viewportWidth / 4 - 10, 2 * (camera.viewportHeight / 3));
        game.batch.end();
    }


    private SelectBox<String> createMapSelectBox() {
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle();
        style.font = selectedFont;
        style.fontColor = Color.BLACK;
        style.scrollStyle = new ScrollPane.ScrollPaneStyle();
        style.listStyle = new List.ListStyle(listFont, Color.RED, Color.WHITE, SELECT_BOX_BACKGROUND);

        SelectBox<String> selectBox = new SelectBox<>(style);

        selectBox.setAlignment(Align.center);
        selectBox.getStyle().listStyle.selection.setLeftWidth(20);

        selectBox.setItems(MAP_LIST);
        selectBox.setSelected(nameifyFile(GameGraphics.mapFileName));

        // Add listener for if selected map changes
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameGraphics.setMap(fileifyName(selectBox.getSelected()));
                mapImg = new TextureRegionDrawable(new Texture(MAP_IMG_FOLDER + GameGraphics.mapFileName + MAP_IMG_EXTENSION));
            }
        });
        // Selection box should always show list (it looks nicer)
        selectBox.addAction(new Action() {
            @Override
            public boolean act(float v) {
                selectBox.showList();
                return false;
            }
        });

        selectBox.setSize(stage.getWidth() / 4f - 10, stage.getHeight() / 20f);
        selectBox.setPosition(5, (4 * stage.getHeight() / 5));

        return selectBox;

    }

    private String fileifyName(String mapName) {
        return mapName.replace(" ", "_").toLowerCase();
    }

    private String nameifyFile(String mapFile) {
        String[] name = mapFile.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : name) {
            builder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
        }
        return builder.toString().substring(0, builder.length() - 1);
    }
}


