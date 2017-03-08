package scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.adt.color.Color;

import base.BaseScene;
import manager.ResourcesManager;
import manager.SceneManager;
import world.CreativeWorld;
import world.World;

public class GameScene extends BaseScene {
	// --------------------------------------------------------------//
	// Variables
	// --------------------------------------------------------------//
	private HUD gameHUD;

	// --------------------------------------------------------------//
	// Class Logic
	// --------------------------------------------------------------//
	@Override
	public void createScene() {
		createBackground();
		createWorld();
		createAnalogOnScreenController();
		createHUD();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.loadMenuScene(engine);
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2);
	}

	// --------------------------------------------------------------//
	// Helper Functions
	// --------------------------------------------------------------//
	private void createBackground() {
		setBackground(new Background(Color.BLUE));
	}

	private void createHUD() {
		gameHUD = new HUD();
		camera.setHUD(gameHUD);
	}

	private void createWorld() {
		World world = new CreativeWorld();
		setChildScene(world);
	}

	private void createAnalogOnScreenController() {
		//TODO
	}

}
