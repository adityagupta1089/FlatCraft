package world;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import manager.ResourcesManager;
import object.player.CreativePlayer;
import object.tile.Tile;

public class CreativeWorld extends World {

	private static final float GRAVITY_X = 0;
	private static final float GRAVITY_Y = 0;

	private static final float PLAYER_DAMPING = 1.5f;

	private static final int GRID_WIDTH = 24;
	private static final int GRID_HEIGHT = 24;
	private static final int DIRT_WIDTH = 5;

	private static final int MAX_DISTANCE = 3;

	private static final int BACKGROUND_TILE_EDGE = 256;
	private static final int BACKGROUND_GRID_WIDTH = GRID_WIDTH * Tile.TILE_EDGE
			/ BACKGROUND_TILE_EDGE;
	private static final int BACKGROUND_GRID_HEIGHT = GRID_HEIGHT * Tile.TILE_EDGE
			/ BACKGROUND_TILE_EDGE;

	public CreativeWorld(BoundCamera camera) {
		super(camera);
		camera.setBounds(0, 0, GRID_WIDTH * Tile.TILE_EDGE, GRID_HEIGHT * Tile.TILE_EDGE);
		camera.setBoundsEnabled(true);
		player.setLinearDamping(PLAYER_DAMPING);
	}

	@Override
	public Vector2 getGravity() {
		return new Vector2(GRAVITY_X, GRAVITY_Y);
	}

	@Override
	public void createForeground() {
		int i = 0;
		/* Layers of dirt */
		for (; i < DIRT_WIDTH; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				createTile(j, i, "DIRT");

			}
		}
		/* One layer of grass */
		for (int j = 0; j < GRID_WIDTH; j++) {
			createTile(j, i, "DIRT_GRASS");
		}
	}

	private void createTile(int i, int j, String type) {
		Position pos = new Position(i, j);
		Tile newTile = new Tile(i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
				j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, type);
		grid.put(pos, newTile);
		this.attachChild(newTile);
		Body body = PhysicsFactory.createBoxBody(physicsWorld, newTile, BodyType.StaticBody,
				fixedSolidObjectFixtureDef);
		bodies.put(pos, body);
	}

	private void deleteTile(int i, int j) {
		Position pos = new Position(i, j);
		physicsWorld.destroyBody(bodies.get(pos));
		bodies.remove(pos);
		Tile t = grid.get(pos);
		grid.remove(pos);
		final EngineLock engineLock = ResourcesManager.engine.getEngineLock();
		engineLock.lock();
		t.detachSelf();
		t.dispose();
		t = null;
		engineLock.unlock();
	}

	@Override
	public void createBackground() {
		setBackground(new Background(Color.WHITE));
		int separationLayer = BACKGROUND_GRID_HEIGHT / 2;
		for (int i = 0; i < BACKGROUND_GRID_HEIGHT + 1; i++) {
			for (int j = 0; j < BACKGROUND_GRID_WIDTH + 1; j++) {
				TextureRegion temp = null;
				if (i < separationLayer) {
					temp = ResourcesManager.skyBoxBottomRegion;
				} else if (i > separationLayer) {
					temp = ResourcesManager.skyBoxTopRegion;
				} else {
					temp = ResourcesManager.skyBoxSideHillsRegion;
				}
				attachChild(new Sprite(j * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2,
						i * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, BACKGROUND_TILE_EDGE,
						BACKGROUND_TILE_EDGE, temp, ResourcesManager.vertexBufferObjectManager));
			}
		}
	}

	@Override
	public void createPlayer(Camera camera) {
		player = new CreativePlayer(GRID_WIDTH / 2 * Tile.TILE_EDGE,
				(DIRT_WIDTH + 1) * Tile.TILE_EDGE, physicsWorld);
		player.setPosition(player.getX() + player.getWidth() / 2,
				player.getY() + player.getHeight() / 2);
		camera.setCenter(player.getX(), player.getY());
		camera.setChaseEntity(player);
		this.attachChild(player);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionUp()) {
			int blockX = ((int) pSceneTouchEvent.getX()) / Tile.TILE_EDGE;
			int blockY = ((int) pSceneTouchEvent.getY()) / Tile.TILE_EDGE;
			// TODO max distance
			if (blockX != ((int) player.getX()) / Tile.TILE_EDGE
					|| blockY != ((int) player.getY()) / Tile.TILE_EDGE) {
				if (placeMode == MODE_DELETE_TILES
						&& grid.containsKey(new Position(blockX, blockY))) {
					deleteTile(blockX, blockY);
					return true;
				} else if (placeMode == MODE_PLACE_TILES
						&& !grid.containsKey(new Position(blockX, blockY))) {
					createTile(blockX, blockY, "DIRT");
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
