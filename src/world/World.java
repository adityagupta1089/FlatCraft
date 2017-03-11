package world;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import manager.ResourcesManager;
import object.Player;
import object.tile.Tile;

public abstract class World extends Scene {

	private static final int GRID_WIDTH = ResourcesManager.WIDTH / Tile.TILE_EDGE;
	private static final int GRID_HEIGHT = ResourcesManager.HEIGHT / Tile.TILE_EDGE;

	private static final int BACKGROUND_TILE_EDGE = 512;
	private static final int BACKGROUND_GRID_WIDTH = ResourcesManager.WIDTH / BACKGROUND_TILE_EDGE;
	private static final int BACKGROUND_GRID_HEIGHT = ResourcesManager.HEIGHT
			/ BACKGROUND_TILE_EDGE;

	private static final float SOLID_OBJECT_DENSITY = 1;
	private static final float SOLID_OBJECT_ELASTICITY = 0;
	private static final float SOLID_OBJECT_FRICTION = 0.5f;

	private Tile[][] grid;
	public Player player;

	protected PhysicsWorld physicsWorld;

	private static final FixtureDef objectFixtureDef = PhysicsFactory
			.createFixtureDef(SOLID_OBJECT_DENSITY, SOLID_OBJECT_ELASTICITY, SOLID_OBJECT_FRICTION);

	public World() {
		createPhysics();
		createBackground();
		createForeground();
		createPlayer();
	}

	private void createPlayer() {
		player = new Player(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2, physicsWorld);
		this.attachChild(player);
	}

	private void createForeground() {
		grid = new Tile[GRID_HEIGHT][GRID_WIDTH];
		int i = 0;
		for (; i < 2; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				grid[i][j] = new Tile(j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
						i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, "DIRT");
				attachChild(grid[i][j]);
				PhysicsFactory.createBoxBody(physicsWorld, grid[i][j], BodyType.StaticBody,
						objectFixtureDef);
			}
		}
		for (int j = 0; j < GRID_WIDTH; j++) {
			grid[i][j] = new Tile(j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
					i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, "DIRT_GRASS");
			attachChild(grid[i][j]);
			PhysicsFactory.createBoxBody(physicsWorld, grid[i][j], BodyType.StaticBody,
					objectFixtureDef);
		}
	}

	private void createBackground() {
		setBackground(new Background(Color.WHITE));
		for (int i = 0; i < BACKGROUND_GRID_HEIGHT + 1; i++) {
			for (int j = 0; j < BACKGROUND_GRID_WIDTH + 1; j++) {
				TextureRegion temp = null;
				if (i < 2 * BACKGROUND_GRID_HEIGHT / 3) {
					temp = ResourcesManager.skyBoxBottomRegion;
				} else if (i > 5 * BACKGROUND_GRID_HEIGHT / 6) {
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

	public void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, getGravity(), false);
		registerUpdateHandler(physicsWorld);
	}

	public abstract Vector2 getGravity();

}
