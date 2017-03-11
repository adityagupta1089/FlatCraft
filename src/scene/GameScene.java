package scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;

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
	private World world;
	public PhysicsWorld physicsWorld;

	// --------------------------------------------------------------//
	// Class Logic
	// --------------------------------------------------------------//
	@Override
	public void createScene() {
		createBackground();
		createWorld();
		createHUD();
		createPhysics();
		createAnalogOnScreenController();
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
		world = new CreativeWorld();
		attachChild(world);
	}

	private void createPhysics() {
		physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0f, -17f), false);
		registerUpdateHandler(physicsWorld);
	}

	private void createAnalogOnScreenController() {

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(200, 200,
				camera, ResourcesManager.mOnScreenControlBaseTextureRegion,
				ResourcesManager.mOnScreenControlKnobTextureRegion, 0.1f, 200,
				vertexBufferObjectManager, new IAnalogOnScreenControlListener() {
					@Override
					public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl,
							final float pValueX, final float pValueY) {
						world.player.setVelocityDirection(pValueX, pValueY);
					}

					@Override
					public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {

					}
				});
		final Sprite controlBase = analogOnScreenControl.getControlBase();
		controlBase.setOffsetCenter(0, 0);
		controlBase.setScale(3f);

		analogOnScreenControl.getControlKnob().setAlpha(0.1f);

		setChildScene(analogOnScreenControl);
	}

}
