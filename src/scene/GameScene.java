package scene;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import base.BaseScene;
import manager.ResourcesManager;

public class GameScene extends BaseScene {
	//--------------------------------------------------------------//
	// Variables
	// --------------------------------------------------------------//
	private HUD gameHUD;

	//--------------------------------------------------------------//
	// Class Logic
	// --------------------------------------------------------------//
	@Override
	public void createScene() {
		createBackground();
		createHUD();
	}

	@Override
	public void onBackKeyPressed() {

	}

	@Override
	public void disposeScene() {

	}

	//--------------------------------------------------------------//
	// Helper Functions
	// --------------------------------------------------------------//
	private void createBackground() {
		attachChild(new Sprite(400, 240, ResourcesManager.menu_background_region, vertexBufferObjectManager) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}

	private void createHUD() {
		gameHUD = new HUD();
		camera.setHUD(gameHUD);
	}

}
