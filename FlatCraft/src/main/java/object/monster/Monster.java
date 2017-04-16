package object.monster;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Monster extends AnimatedSprite {

    public Monster(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager
            pVertexBufferObjectManager) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
    }
}