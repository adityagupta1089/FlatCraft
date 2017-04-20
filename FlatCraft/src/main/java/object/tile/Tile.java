package object.tile;

import org.andengine.entity.sprite.Sprite;

import manager.ResourcesManager;

public class Tile extends Sprite {

    public static int TILE_EDGE = 100;

    public int mTileType;

    public Tile(float pX, float pY, int pTileType) {
        super(pX, pY, TILE_EDGE, TILE_EDGE, ResourcesManager.tileRegions.get(pTileType),
                ResourcesManager.vertexBufferObjectManager);
        mTileType = pTileType;
        this.setCullingEnabled(true);
    }

}
