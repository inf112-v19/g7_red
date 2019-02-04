package no.uib.inf112.core.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import no.uib.inf112.core.player.Direction;
import no.uib.inf112.core.player.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class TiledMapHandler implements MapHandler {


    private final OrthographicCamera boardCamera;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;

    private final TiledMapTileLayer boardLayer;
    private final TiledMapTileLayer entityLayer;


    //A map of all know entities and their last know location
    private Map<Entity, Vector2> entities;

    private int mapWidth;
    private int mapHeight;
    private int tileWidth;
    private int tileHeight;

    private float zoomSensitivity;
    private float maxZoom;
    private float minZoom;


    /**
     * TODO make the zoom properties be part of the map file
     *
     * @param map The relative path from assets folder to the Tiled map file
     * @throws IllegalArgumentException if max zoom is less than min zoom
     */
    public TiledMapHandler(String map) {
        try {
            TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
            params.textureMagFilter = Texture.TextureFilter.Linear;
            params.textureMinFilter = Texture.TextureFilter.Linear;
            tiledMap = new TmxMapLoader().load(map, params);
        } catch (final Exception e) {
            throw new IllegalArgumentException("Failed to load map at '" + map + "'");
        }

        mapWidth = tiledMap.getProperties().get("width", int.class);
        mapHeight = tiledMap.getProperties().get("height", int.class);
        tileWidth = tiledMap.getProperties().get("tilewidth", int.class);
        tileHeight = tiledMap.getProperties().get("tileheight", int.class);

        zoomSensitivity = tiledMap.getProperties().get(ZOOM_SENSITIVITY_PATH, DEFAULT_ZOOM_SENSITIVITY, float.class);
        maxZoom = tiledMap.getProperties().get(MAX_ZOOM_PATH, DEFAULT_MAX_ZOOM, float.class);
        minZoom = tiledMap.getProperties().get(MIN_ZOOM_PATH, DEFAULT_MIN_ZOOM, float.class);

        if (maxZoom < minZoom) {
            throw new IllegalArgumentException(
                "Max (" + maxZoom + ") zoom cannot be less than min zoom (" + minZoom + ")");
        }

        for (MapLayer layer : tiledMap.getLayers()) {
            if (!(layer instanceof TiledMapTileLayer)) {
                throw new IllegalArgumentException(
                    "One or more of the layer in the map " + map + " is not a TiledMapTileLayer");
            }
        }


        //TODO check class cast exception
        boardLayer = (TiledMapTileLayer) tiledMap.getLayers().get(BOARD_LAYER_NAME);
        if (boardLayer == null) {
            throw new IllegalStateException("Given tiled map does not have a board layer named " + BOARD_LAYER_NAME);
        }

        //create a new empty layer for all the robots to play on :)
        entityLayer = new TiledMapTileLayer(mapWidth, mapHeight, tileWidth, tileHeight);
        tiledMap.getLayers().add(entityLayer);

        renderer = new OrthogonalTiledMapRenderer(tiledMap);

        boardCamera = new OrthographicCamera();
        resize();

        //use a linked hashmap to make sure the iteration is consistent
        entities = new LinkedHashMap<>();
    }

    @Override
    public void resize() {
        boardCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(@NotNull Batch batch) {
        boardCamera.update();
        batch.setProjectionMatrix(boardCamera.combined);
        renderer.setView(boardCamera);
        renderer.render();
    }

    @Override
    public void update(float delta) {
        //remove all known entity sprites
        for (Map.Entry<Entity, Vector2> entry : entities.entrySet()) {
            entityLayer.setCell((int) entry.getValue().x, (int) entry.getValue().y, null);
        }
        //set new pos
        for (Map.Entry<Entity, Vector2> entry : entities.entrySet()) {
            if(entry.getKey().getX() == entry.getValue().x && entry.getKey().getY() == entry.getValue().y){
                continue;
            }

            entry.setValue(setEntityOnBoard(entry.getKey(), entry.getValue()));
        }
    }

    @NotNull
    @Override
    public TiledMapTile getBoardLayerTile(int x, int y) {
        return boardLayer.getCell(x, y).getTile();
    }

    @Override
    @Nullable
    public Entity getEntity(int x, int y) {
        Vector2 v = new Vector2(x, y);
        for (Map.Entry<Entity, Vector2> entry : entities.entrySet()) {
            if (v.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public TiledMapTileSets getMapTileSets() {
        return tiledMap.getTileSets();
    }

    @Override
    public void addEntity(@NotNull Entity entity) {
        for (Entity knownRobot : getEntities()) {
            if (entity.getX() == knownRobot.getX() && entity.getY() == knownRobot.getY()) {
                throw new IllegalStateException("Cannot add an entity on top of another entity");
            }
        }
        //add the entity last in the array
        entities.put(entity, new Vector2(entity.getX(), entity.getY()));
    }

    @Override
    public boolean removeEntity(Entity entity) {
        return entities.remove(entity) == null;
    }

    @NotNull
    @Override
    public Set<Entity> getEntities() {
        return entities.keySet();
    }

    @Override
    public boolean isOutsideBoard(int x, int y) {
        return x < 0 || x >= getMapWidth() || y < 0 | y >= getMapHeight();
    }

    @Override
    public int getMapWidth() {
        return mapWidth;
    }

    @Override
    public int getMapHeight() {
        return mapHeight;
    }

    @Override
    public void moveCamera(float dx, float dy) {
        boardCamera.position.x += dx * boardCamera.zoom;
        boardCamera.position.y += dy * boardCamera.zoom;
        //TODO make sure the camera is within a reasonable distance of the board edges
    }

    @Override
    public void zoom(int direction) {
        if (direction == 0) {
            throw new IllegalArgumentException("Zoom direction cannot be 0");
        }
        float delta = Math.signum(direction) * zoomSensitivity;
        boardCamera.zoom += delta;
        if (boardCamera.zoom > maxZoom) {
            boardCamera.zoom = maxZoom;
        }
        else if (boardCamera.zoom < minZoom) {
            boardCamera.zoom = minZoom;
        }
    }

    @Override
    public void dispose() {
        renderer.dispose();
        tiledMap.dispose();
    }


    private Vector2 setEntityOnBoard(@NotNull Entity entity, @NotNull Vector2 oldPos) {
        if (entity.getTile() == null) { return null; }
        if (isOutsideBoard(entity.getX(), entity.getY())) {
            throw new IllegalArgumentException(
                "Given location (" + entity.getX() + ", " + entity.getY() + ") is out of bounds");
        }
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell().setTile(entity.getTile());
        entityLayer.setCell(entity.getX(), entity.getY(), cell);


        Direction dir = entity.getDirection();

        int dx = (int) (entity.getX() - oldPos.x);
        int dy = (int) (entity.getY() - oldPos.y);
        if(dx > 0){
            dir = Direction.EAST;
        }else if(dx < 0 ){
            dir = Direction.WEST;
        }else {
            if (dy > 0) {
                dir = Direction.NORTH;
            } else if (dy < 0) {
                dir = Direction.SOUTH;
            }
        }
        entity.setDirection(dir);

        return new Vector2(entity.getX(),entity.getY());
    }
}