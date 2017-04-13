package world;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import android.util.SparseArray;
import hud.InventoryItem;
import manager.ResourcesManager;
import object.player.Player;
import object.tile.Tile;

public abstract class World extends Scene {

	private static final float SOLID_OBJECT_DENSITY = 1;
	private static final float SOLID_OBJECT_ELASTICITY = 0;
	private static final float SOLID_OBJECT_FRICTION = 0.5f;

	protected static final int GRID_WIDTH = 30;
	protected static final int GRID_HEIGHT = 30;

	protected int placeMode;

	public static final int MODE_PLACE_TILES = 0;
	public static final int MODE_DELETE_TILES = 1;

	protected SparseArray<Tile> grid;
	protected SparseArray<Body> bodies;
	public Player player;

	protected Set<IEntity> entities;

	protected PhysicsWorld physicsWorld;

	protected static final FixtureDef fixedSolidObjectFixtureDef = PhysicsFactory
			.createFixtureDef(SOLID_OBJECT_DENSITY, SOLID_OBJECT_ELASTICITY, SOLID_OBJECT_FRICTION);

	public World(Camera camera) {
		entities = new HashSet<IEntity>();

		createPhysics();

		grid = new SparseArray<Tile>();
		bodies = new SparseArray<Body>();

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
		return GRID_WIDTH * i + j;
	}

	protected void createTile(int i, int j, int type) {
		ResourcesManager.placeBlockSound.play();
		int pos = position(i, j);
		Tile newTile = new Tile(i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
				j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, type);
		newTile.setPosition(i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
				j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2);
		grid.put(pos, newTile);
		this.attachChild(newTile);
		Body body = PhysicsFactory.createBoxBody(physicsWorld, newTile, BodyType.StaticBody,
				fixedSolidObjectFixtureDef);
		bodies.put(pos, body);
	}

	protected void deleteTile(int i, int j) {
		ResourcesManager.deleteBlockSound.play();
		int pos = position(i, j);
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