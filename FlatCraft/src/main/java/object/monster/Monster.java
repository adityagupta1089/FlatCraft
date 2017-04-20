package object.monster;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import object.tile.Tile;

public class Monster extends AnimatedSprite {

    public Monster(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager
            pVertexBufferObjectManager) {
        super(pX, pY, Tile.TILE_EDGE, Tile.TILE_EDGE, pTiledTextureRegion, pVertexBufferObjectManager);
    }
}