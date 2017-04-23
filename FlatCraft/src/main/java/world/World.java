package world;

import android.util.SparseArray;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hud.InventoryItem;
import manager.ResourcesManager;
import object.player.Player;
import object.tile.Tile;

public abstract class World extends Scene {

    public static final int MODE_PLACE_TILES = 0;
    public static final int MODE_DELETE_TILES = 1;
    protected static final int GRID_WIDTH = 20;
    protected static final int GRID_HEIGHT = 20;
    protected static final int BACKGROUND_TILE_EDGE = 256;
    protected static final int BACKGROUND_GRID_WIDTH = GRID_WIDTH * Tile.TILE_EDGE /
            BACKGROUND_TILE_EDGE;
    protected static final int BACKGROUND_GRID_HEIGHT = GRID_HEIGHT * Tile.TILE_EDGE /
            BACKGROUND_TILE_EDGE;
    private static final float SOLID_OBJECT_DENSITY = 1;
    private static final float SOLID_OBJECT_ELASTICITY = 0;
    private static final float SOLID_OBJECT_FRICTION = 0.5f;
    protected static final FixtureDef fixedSolidObjectFixtureDef = PhysicsFactory
            .createFixtureDef(SOLID_OBJECT_DENSITY, SOLID_OBJECT_ELASTICITY, SOLID_OBJECT_FRICTION);
    public Player player;
    protected int placeMode;
    protected SparseArray<Tile> grid;
    protected SparseArray<Body> bodies;
    protected Set<IEntity> entities;
    protected PhysicsWorld physicsWorld;

    protected World() {
        entities = new HashSet<>();

        createPhysics();

        grid = new SparseArray<>();
        bodies = new SparseArray<>();

        placeMode = MODE_PLACE_TILES;
    }

    public World(Camera camera) {
        entities = new HashSet<>();

        createPhysics();

        grid = new SparseArray<>();
        bodies = new SparseArray<>();

        createBackground();
        createForeground();

        createPlayer(camera);

        placeMode = MODE_PLACE_TILES;
    }

    public abstract void createPlayer(Camera camera);

    public void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, getGravity(), false);
        registerUpdateHandler(physicsWorld);
    }

    public abstract Vector2 getGravity();

    public abstract void createBackground();

    public abstract void createForeground();

    public abstract boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent);

    public void setPlaceMode(int modePlaceTiles) {
        this.placeMode = modePlaceTiles;
    }

    public abstract void onPopulateQuickAccess(List<InventoryItem> quickAccess);

    protected int position(int i, int j) {
        return (GRID_WIDTH + 1) * i + j;
    }

    private static final float TOTAL_TIME = 30f;

    protected void createTile(final int i, final int j, int type, boolean userData, boolean vanish) {
        ResourcesManager.placeBlockSound.play();
        int pos = position(i, j);
        final Tile newTile = new Tile(i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
                j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, type);
        newTile.setPosition(i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
                j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2);
        grid.put(pos, newTile);
        this.attachChild(newTile);
        Body body = PhysicsFactory.createBoxBody(physicsWorld, newTile, BodyType.StaticBody,
                fixedSolidObjectFixtureDef);
        if (userData) body.setUserData("tile");
        bodies.put(pos, body);
        if (vanish) {
            newTile.registerUpdateHandler(new TimerHandler(0.1f, true, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    if (pTimerHandler.getTimerSecondsElapsed() < TOTAL_TIME)
                        newTile.setAlpha(newTile.getAlpha() - 0.1f / TOTAL_TIME);
                    else
                        newTile.unregisterUpdateHandler(pTimerHandler);
                }
            }));
            this.registerUpdateHandler(new TimerHandler(TOTAL_TIME, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    deleteTile(i, j);
                }
            }));
        }
    }

    protected void createTile(int i, int j, int type) {
        createTile(i, j, type, true, false);
    }

    protected void createTile(int i, int j, int type, boolean userData) {
        createTile(i, j, type, userData, false);
    }

    protected void deleteTile(int i, int j) {
        ResourcesManager.deleteBlockSound.play();
        int pos = position(i, j);
        if (bodies.indexOfKey(pos) < 0) return;
        physicsWorld.destroyBody(bodies.get(pos));
        bodies.remove(pos);
        Tile t = grid.get(pos);
        grid.remove(pos);
        final EngineLock engineLock = ResourcesManager.engine.getEngineLock();
        engineLock.lock();
        entities.remove(t);
        t.detachSelf();
        t.dispose();
        t = null;
        engineLock.unlock();
    }

    public void cleanEntities() {
        for (IEntity entity : entities) {
            entity.clearEntityModifiers();
            entity.clearUpdateHandlers();
            entity.detachSelf();

            if (!entity.isDisposed()) {
                entity.dispose();
            }
        }

        entities.clear();
        entities = null;
    }

    @Override
    public void dispose() {
        ResourcesManager.engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                cleanEntities();
                clearTouchAreas();
                clearUpdateHandlers();
                System.gc();
            }
        });
    }
}
