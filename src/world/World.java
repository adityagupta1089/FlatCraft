package world;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;

import manager.ResourcesManager;
import object.Player;
import object.tile.Tile;

public class World extends Scene {

	private static final int GRID_WIDTH = ResourcesManager.WIDTH / Tile.TILE_EDGE;
	private static final int GRID_HEIGHT = ResourcesManager.HEIGHT / Tile.TILE_EDGE;

	private static final int BACKGROUND_TILE_EDGE = 512;
	private static final int BACKGROUND_GRID_WIDTH = ResourcesManager.WIDTH / BACKGROUND_TILE_EDGE;
	private static final int BACKGROUND_GRID_HEIGHT = ResourcesManager.HEIGHT / BACKGROUND_TILE_EDGE;

	private Tile[][] grid;
	Player player;

	public World() {
		createBackground();
		createForeground();

	}

	private void createForeground() {
		grid = new Tile[GRID_HEIGHT][GRID_WIDTH];
		int i = 0;
		for (; i < 2; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				grid[i][j] = new Tile(j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, "DIRT");
				attachChild(grid[i][j]);
			}
		}
		for (int j = 0; j < GRID_WIDTH; j++) {
			grid[i][j] = new Tile(j * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, i * Tile.TILE_EDGE + Tile.TILE_EDGE / 2, "DIRT_GRASS");
			attachChild(grid[i][j]);
		}

		player = new Player(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2);
		this.attachChild(player);
	}

	private void createBackground() {
		setBackground(new Background(Color.WHITE));
		for (int i = 0; i <= BACKGROUND_GRID_HEIGHT; i++) {
			for (int j = 0; j <= BACKGROUND_GRID_WIDTH; j++) {
				TextureRegion temp = null;
				if (i < 2 * BACKGROUND_GRID_HEIGHT / 3) {
					temp = ResourcesManager.skyBoxBottomRegion;
				} else if (i > 5 * BACKGROUND_GRID_HEIGHT / 6) {
					temp = ResourcesManager.skyBoxTopRegion;
				} else {
					temp = ResourcesManager.skyBoxSideHillsRegion;
				}
				attachChild(new Sprite(j * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, i * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, BACKGROUND_TILE_EDGE, BACKGROUND_TILE_EDGE, temp, ResourcesManager.vertexBufferObjectManager));
			}
		}
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		super.onManagedUpdate(pSecondsElapsed);
	}

}
