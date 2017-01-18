package scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.adt.color.Color;

import base.BaseScene;

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
		setBackground(new Background(Color.BLUE));
	}

	private void createHUD() {
		gameHUD = new HUD();
		camera.setHUD(gameHUD);
	}

}
