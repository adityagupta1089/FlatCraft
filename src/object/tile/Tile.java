package object.tile;

import org.andengine.entity.sprite.Sprite;

import manager.ResourcesManager;

public class Tile extends Sprite {

	public static int TILE_EDGE = 64;

	public boolean passable;

	public Tile(float pX, float pY, String pTileType) {
		super(pX, pY, TILE_EDGE, TILE_EDGE, ResourcesManager.tileRegions.get(pTileType),
				ResourcesManager.vertexBufferObjectManager);
		this.passable = ResourcesManager.tilePassability.get(pTileType);
	}

}
