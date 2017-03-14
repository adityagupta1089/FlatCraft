package scene;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;

import base.BaseScene;
import manager.ResourcesManager;
import manager.SceneManager;
import world.CreativeWorld;
import world.World;

public class GameScene extends BaseScene implements IOnSceneTouchListener, IOnAreaTouchListener {
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

		this.setOnSceneTouchListener(this);
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.loadMenuScene(engine);
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setChaseEntity(null);
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
		world = new CreativeWorld(camera);
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
		ButtonSprite placeTilesYes = new ButtonSprite(1600, 200,
				ResourcesManager.placeTilesYesRegion, vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					world.setPlaceMode(World.MODE_PLACE_TILES);
					return true;
				}
				return false;
			}
		};
		ButtonSprite placeTilesNo = new ButtonSprite(1600, 400, ResourcesManager.placeTilesNoRegion,
				vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					world.setPlaceMode(World.MODE_DELETE_TILES);
					return true;
				}
				return false;
			}
		};
		gameHUD.attachChild(placeTilesYes);
		gameHUD.attachChild(placeTilesNo);
		gameHUD.registerTouchArea(placeTilesYes);
		gameHUD.registerTouchArea(placeTilesNo);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return world.onSceneTouchEvent(pScene, pSceneTouchEvent);
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
			float pTouchAreaLocalX, float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		return false;
	}
}
