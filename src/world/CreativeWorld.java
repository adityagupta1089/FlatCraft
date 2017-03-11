package world;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import manager.ResourcesManager;
import object.player.CreativePlayer;
import object.tile.Tile;

public class CreativeWorld extends World {

	private static final float GRAVITY_X = 0;
	private static final float GRAVITY_Y = 0;

	private static final float PLAYER_DAMPING = 1.5f;

	private static final int GRID_WIDTH = 100;
	private static final int GRID_HEIGHT = 100;

	private static final int BACKGROUND_TILE_EDGE = 256;
	private static final int BACKGROUND_GRID_WIDTH = GRID_WIDTH * Tile.TILE_EDGE
			/ BACKGROUND_TILE_EDGE;
	private static final int BACKGROUND_GRID_HEIGHT = GRID_HEIGHT * Tile.TILE_EDGE
			/ BACKGROUND_TILE_EDGE;

	public CreativeWorld(Camera camera) {
		super(camera);
		player.setLinearDamping(PLAYER_DAMPING);
	}

	@Override
	public Vector2 getGravity() {
		return new Vector2(GRAVITY_X, GRAVITY_Y);
	}

	@Override
	public void createForeground() {
		grid = new Tile[GRID_HEIGHT][GRID_WIDTH];
		int i = 0;
		/* Two layers of dirt */
		for (; i < 2; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				grid[i][j] = new Tile(j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
						i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, "DIRT");
				attachChild(grid[i][j]);
				PhysicsFactory.createBoxBody(physicsWorld, grid[i][j], BodyType.StaticBody,
						fixedSolidObjectFixtureDef);
			}
		}
		/* One layer of grass */
		for (int j = 0; j < GRID_WIDTH; j++) {
			grid[i][j] = new Tile(j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2,
					i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, "DIRT_GRASS");
			attachChild(grid[i][j]);
			PhysicsFactory.createBoxBody(physicsWorld, grid[i][j], BodyType.StaticBody,
					fixedSolidObjectFixtureDef);
		}
	}

	@Override
	public void createBackground() {
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

	@Override
	public void createPlayer(Camera camera) {
		player = new CreativePlayer(GRID_WIDTH / 2 * Tile.TILE_EDGE, 3 * Tile.TILE_EDGE,
				physicsWorld);
		camera.setCenter(player.getX(), player.getY());
		camera.setChaseEntity(player);
		this.attachChild(player);
	}

}
