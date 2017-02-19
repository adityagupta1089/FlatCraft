package world;

import org.andengine.entity.scene.Scene;

import object.Player;
import object.tile.Tile;

public class World extends Scene {

	private static final int GRID_WIDTH = 10;
	private static final int GRID_HEIGHT = 10;

	private Tile[][] grid;
	Player player;

	public World() {
		grid = new Tile[GRID_HEIGHT][GRID_WIDTH];
		//TODO player = new Player(0, 0, 0, 0, null, null);
		for (int i = 0; i < GRID_HEIGHT; i++) {
			for (int j = 0; j < GRID_WIDTH; j++) {
				attachChild(new Tile(j * Tile.TILE_WIDTH, i * Tile.TILE_HEIGHT, "BRICK_RED"));
			}
		}
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		// TODO Auto-generated method stub
		super.onManagedUpdate(pSecondsElapsed);
	}

}
