package world;

import java.util.List;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;

import hud.InventoryItem;
import manager.ResourcesManager;
import object.player.CreativePlayer;
import object.tile.Tile;
import spritesheet.TileSpritesheet;

public class CreativeWorld extends World {

	//@formatter:off
	private static final float GRAVITY_X = 0;
	private static final float GRAVITY_Y = 0;

	private static final float PLAYER_DAMPING = 1.5f;
	
	private static final int DIRT_WIDTH = 10;

	//private static final int MAX_DISTANCE = 3;

	private static final int BACKGROUND_TILE_EDGE = 256;
	private static final int BACKGROUND_GRID_WIDTH = GRID_WIDTH * Tile.TILE_EDGE / BACKGROUND_TILE_EDGE;
	private static final int BACKGROUND_GRID_HEIGHT = GRID_HEIGHT * Tile.TILE_EDGE / BACKGROUND_TILE_EDGE;
	//@formatter:on

	// private int tileNum = 0;

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
				createTile(j, i, TileSpritesheet.DIRT_ID);

			}
		}
		/* One layer of grass */
		for (int j = 0; j < GRID_WIDTH; j++) {
			createTile(j, i, TileSpritesheet.DIRT_GRASS_ID);
		}
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
				//@formatter:off
				Sprite bgtile = new Sprite(j * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, i * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, BACKGROUND_TILE_EDGE, BACKGROUND_TILE_EDGE, temp, ResourcesManager.vertexBufferObjectManager);
				//@formatter:on
				attachChild(bgtile);
				entities.add(bgtile);
			}
		}
	}

	@Override
	public void createPlayer(Camera camera) {
		//@formatter:off
		player = new CreativePlayer(GRID_WIDTH / 2 * Tile.TILE_EDGE, (DIRT_WIDTH + 1) * Tile.TILE_EDGE, physicsWorld);
		player.setPosition(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
		//@formatter:on
		camera.setCenter(player.getX(), player.getY());
		camera.setChaseEntity(player);
		this.attachChild(player);
		entities.add(player);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		// TODO infinite count, increase when deleted
		if (pSceneTouchEvent.isActionUp()) {
			int blockX = ((int) pSceneTouchEvent.getX()) / Tile.TILE_EDGE;
			int blockY = ((int) pSceneTouchEvent.getY()) / Tile.TILE_EDGE;
			// int playerX = ((int) player.getX()) / Tile.TILE_EDGE;
			// int playerY = ((int) player.getY()) / Tile.TILE_EDGE;
			// if (Math.abs(playerX - blockX) + Math.abs(playerY - blockY) > MAX_DISTANCE)
			// return false;
			//@formatter:off
			if (blockX != ((int) player.getX()) / Tile.TILE_EDGE || blockY != ((int) player.getY()) / Tile.TILE_EDGE) {
				if (placeMode == MODE_DELETE_TILES && grid.indexOfKey(position(blockX, blockY)) > 0) {
					deleteTile(blockX, blockY);
					return true;
				} else if (placeMode == MODE_PLACE_TILES && grid.indexOfKey(position(blockX, blockY)) < 0) {
					if(ResourcesManager.hud.currItem.take()){
						createTile(blockX, blockY,  ResourcesManager.hud.currItem.mTileType);
						return true;
					}else{
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
			//@formatter:on
		} else {
			return false;
		}
	}

	@Override
	public void onPopulateQuickAccess(List<InventoryItem> qa) {
		// TODO add more inventory items
		for (int i = TileSpritesheet.MIN_INDEX; i < TileSpritesheet.MAX_INDEX; i++) {
			qa.add(new InventoryItem(i, 100));
		}
		/*
		 * qa.add(new InventoryItem("BRICK_RED", 100));
		 * qa.add(new InventoryItem("COTTON_BLUE", 100));
		 * qa.add(new InventoryItem("COTTON_GREEN", 100));
		 * qa.add(new InventoryItem("COTTON_RED", 100));
		 * qa.add(new InventoryItem("COTTON_TAN", 100));
		 * qa.add(new InventoryItem("FENCE_WOOD", 100));
		 * qa.add(new InventoryItem("STONE", 100));
		 * qa.add(new InventoryItem("WOOD", 100));
		 */

	}

}
