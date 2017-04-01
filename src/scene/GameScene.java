package scene;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import base.BaseScene;
import hud.FlatCraftHUD;
import manager.ResourcesManager;
import manager.SceneManager;
import world.CreativeWorld;
import world.World;

public class GameScene extends BaseScene implements IOnSceneTouchListener {
	// --------------------------------------------------------------//
	// Variables
	// --------------------------------------------------------------//
	private FlatCraftHUD gameHUD;
	private World world;
	public PhysicsWorld physicsWorld;

	private TiledSprite placeTilesYes;
	private TiledSprite placeTilesNo;

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
		gameHUD = new FlatCraftHUD();
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

		placeTilesYes = new TiledSprite(1700, 180, ResourcesManager.placeTilesYesRegion,
				vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					world.setPlaceMode(World.MODE_PLACE_TILES);
					setMode(World.MODE_PLACE_TILES);
					return true;
				}
				return false;
			}

		};
		placeTilesYes.setCurrentTileIndex(1);

		placeTilesNo = new TiledSprite(1700, 380, ResourcesManager.placeTilesNoRegion,
				vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					world.setPlaceMode(World.MODE_DELETE_TILES);
					setMode(World.MODE_DELETE_TILES);
					return true;
				}
				return false;
			}
		};
		TiledSprite soundSprite = new TiledSprite(1500, 900, ResourcesManager.soundRegion,
				vertexBufferObjectManager) {

			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					this.setCurrentTileIndex(1 - this.getCurrentTileIndex());
					// TODO change sound
					return true;
				}
				return false;
			}
		};

		Sprite menuSprite = new Sprite(1700, 900, ResourcesManager.menuRegion,
				vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {

					ResourcesManager.gameActivity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
										case DialogInterface.BUTTON_POSITIVE:
											onBackKeyPressed();
											break;

										case DialogInterface.BUTTON_NEGATIVE:
											break;
									}
								}
							};
							AlertDialog.Builder builder = new AlertDialog.Builder(
									ResourcesManager.gameActivity);
							builder.setMessage("Are you sure?")
									.setPositiveButton("Yes", dialogClickListener)
									.setNegativeButton("No", dialogClickListener).show();
						}
					});

					return true;
				}
				return false;
			}
		};

		Sprite pauseSprite = new Sprite(200, 900, ResourcesManager.pauseRegion,
				vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					// TODO pause
					return true;
				}
				return false;
			}
		};

		placeTilesYes.setScale(1.5f);
		placeTilesNo.setScale(1.5f);

		soundSprite.setScale(1.5f * 88f / 100f);

		pauseSprite.setScale(1.5f * 88f / 100f);
		menuSprite.setScale(1.5f * 88f / 100f);

		gameHUD.attachChild(placeTilesYes);
		gameHUD.attachChild(placeTilesNo);
		gameHUD.attachChild(soundSprite);

		gameHUD.attachChild(pauseSprite);
		gameHUD.attachChild(menuSprite);

		gameHUD.registerTouchArea(placeTilesYes);
		gameHUD.registerTouchArea(placeTilesNo);
		gameHUD.registerTouchArea(soundSprite);

		gameHUD.registerTouchArea(pauseSprite);
		gameHUD.registerTouchArea(menuSprite);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		return world.onSceneTouchEvent(pScene, pSceneTouchEvent);
	}

	private void setMode(int modePlaceTiles) {
		if (modePlaceTiles == World.MODE_PLACE_TILES) {
			placeTilesYes.setCurrentTileIndex(1);
			placeTilesNo.setCurrentTileIndex(0);
		} else if (modePlaceTiles == World.MODE_DELETE_TILES) {
			placeTilesYes.setCurrentTileIndex(0);
			placeTilesNo.setCurrentTileIndex(1);
		}
	}
}
