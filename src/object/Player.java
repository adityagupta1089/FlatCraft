package object;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;

public class Player extends Sprite {

	public Player(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
			ISpriteVertexBufferObject pSpriteVertexBufferObject) {
		super(pX, pY, pWidth, pHeight, pTextureRegion, pSpriteVertexBufferObject);
		// TODO Auto-generated constructor stub
	}

}
