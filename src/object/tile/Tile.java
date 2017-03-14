package object.tile;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import manager.ResourcesManager;

public class Tile extends Sprite {

	public static int TILE_EDGE = 100;

	public boolean passable;

	public Tile(float pX, float pY, String pTileType) {
		super(pX, pY, TILE_EDGE, TILE_EDGE, ResourcesManager.tileRegions.get(pTileType),
				ResourcesManager.vertexBufferObjectManager);
		this.passable = ResourcesManager.tilePassability.get(pTileType);
		this.setCullingEnabled(true);
	}

	@Override
	protected void preDraw(GLState pGLState, Camera pCamera) {
		super.preDraw(pGLState, pCamera);
		pGLState.enableDither();
	}

}
