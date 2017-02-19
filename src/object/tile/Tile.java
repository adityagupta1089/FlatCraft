package object.tile;

import org.andengine.entity.sprite.Sprite;

import manager.ResourcesManager;

public class Tile extends Sprite {

	private static int TILE_WIDTH = 64;
	private static int TILE_HEIGHT = 64;

	public boolean passable;

	public Tile(float pX, float pY, String pTileType) {
		super(pX, pY, TILE_WIDTH, TILE_HEIGHT, ResourcesManager.tileRegions.get("BRICK_RED"),
				ResourcesManager.vertexBufferObjectManager);
		this.passable = ResourcesManager.tilePassability.get(pTileType);
	}

}
