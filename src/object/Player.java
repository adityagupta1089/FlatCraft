package object;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;

import manager.ResourcesManager;

public class Player extends Sprite {

	public Player(float pX, float pY) {
		super(pX, pY, 64, 64, ResourcesManager.playerRegion, ResourcesManager.vertexBufferObjectManager);
	}

}
