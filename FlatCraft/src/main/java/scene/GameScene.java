package scene;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import base.BaseScene;
import hud.FlatCraftHUD;
import manager.ResourcesManager;
import manager.SceneManager;
import world.CreativeWorld;
import world.MultiPlayerWorld;
import world.SurvivalWorld;
import world.World;

public class GameScene extends BaseScene implements IOnSceneTouchListener, GameModes {
    // --------------------------------------------------------------//
    // Variables
    // --------------------------------------------------------------//
    private FlatCraftHUD gameHUD;
    private World world;
    private PhysicsWorld physicsWorld;

    private TiledSprite placeTilesYes;
    private TiledSprite placeTilesNo;

    private List<IEntity> entities;

    private static int mode;

    // --------------------------------------------------------------//
    // Class Logic
    // --------------------------------------------------------------//
    @Override
    public void createScene() {
        engine.getMusicManager().setMasterVolume(ResourcesManager.mfxVol);
        engine.getSoundManager().setMasterVolume(ResourcesManager.sfxVol);

        entities = new LinkedList<>();

        createBackground();
        createWorld();
        createHUD();
        createPhysics();
        createAnalogOnScreenController();

        this.setOnSceneTouchListener(this);

        createFPSCounter();
    }

    private void createFPSCounter() {
        final FPSCounter fpsCounter = new FPSCounter();
        this.engine.registerUpdateHandler(fpsCounter);
        final Text fpsText = new Text(250, 1000, ResourcesManager.caviarDreams, "FPS: XX.XX",
                vertexBufferObjectManager);

        gameHUD.attachChild(fpsText);

        gameHUD.registerUpdateHandler(new TimerHandler(1 / 20f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                fpsText.setText("FPS: " + String.format("%2.2f", fpsCounter.getFPS()));
            }
        }));
    }

    @Override
    public void onBackKeyPressed() {
        ResourcesManager.buttonClickSound.play();
        if (ResourcesManager.gameMusic.isPlaying()) {
            ResourcesManager.menuMusic.pause();
        }
        SceneManager.loadMenuScene(engine);
    }

    private void clearPhysicsWorld(PhysicsWorld physicsWorld) {
        Iterator<Body> localIterator = physicsWorld.getBodies();
        while (true) {
            if (!localIterator.hasNext()) {
                physicsWorld.clearForces();
                physicsWorld.clearPhysicsConnectors();
                physicsWorld.reset();
                physicsWorld.dispose();
                physicsWorld = null;
                return;
            }
            try {
                physicsWorld.destroyBody(localIterator.next());
            } catch (Exception e) {
                Debug.e(e);
            }
        }
    }

    @Override
    public void attachChild(IEntity pEntity) throws IllegalStateException {
        entities.add(pEntity);
        super.attachChild(pEntity);
    }

    private void cleanEntities() {
        for (IEntity entity : entities) {
            entity.clearEntityModifiers();
            entity.clearUpdateHandlers();
            entity.detachSelf();

            if (!entity.isDisposed()) {
                entity.dispose();
            }
        }

        entities.clear();
        entities = null;
    }

    private void clearScene() {
        engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                clearPhysicsWorld(physicsWorld);
                cleanEntities();
                clearTouchAreas();
                clearUpdateHandlers();
                System.gc();
            }
        });
    }

    @Override
    public void disposeScene() {
        if (ResourcesManager.gameMusic.isPlaying()) ResourcesManager.gameMusic.pause();
        camera.setHUD(null);
        camera.setChaseEntity(null);
        camera.setCenter(ResourcesManager.WIDTH / 2, ResourcesManager.HEIGHT / 2);
        clearScene();
    }

    // --------------------------------------------------------------//
    // Helper Functions
    // --------------------------------------------------------------//
    private void createBackground() {
        setBackground(new Background(Color.BLUE));
    }

    private void createHUD() {
        gameHUD = new FlatCraftHUD();
        ResourcesManager.hud = gameHUD;
        camera.setHUD(gameHUD);
        gameHUD.initTiles();
    }

    private void createWorld() {
        if (mode == MODE_SINGLE_CREATIVE)
            world = new CreativeWorld(camera);
        else if (mode == MODE_MULTI_PLAYER)
            world = new MultiPlayerWorld(camera);
        else if (mode == MODE_SINGLE_SURVIVAL)
            world = new SurvivalWorld(camera);
        ResourcesManager.world = world;
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
                    ResourcesManager.buttonClickSound.play();
                    world.setPlaceMode(World.MODE_PLACE_TILES);
                    setTilePlacementMode(World.MODE_PLACE_TILES);
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
                    ResourcesManager.buttonClickSound.play();
                    world.setPlaceMode(World.MODE_DELETE_TILES);
                    setTilePlacementMode(World.MODE_DELETE_TILES);
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
                    ResourcesManager.buttonClickSound.play();
                    this.setCurrentTileIndex(1 - this.getCurrentTileIndex());
                    if (ResourcesManager.gameMusic.isPlaying()) ResourcesManager.gameMusic.pause();
                    else ResourcesManager.gameMusic.play();
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
                    ResourcesManager.buttonClickSound.play();
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
                                            ResourcesManager.buttonClickSound.play();
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

        placeTilesYes.setScale(1.5f);
        placeTilesNo.setScale(1.5f);

        soundSprite.setScale(1.5f * 88f / 100f);

        menuSprite.setScale(1.5f * 88f / 100f);

        gameHUD.attachChild(placeTilesYes);
        gameHUD.attachChild(placeTilesNo);
        gameHUD.attachChild(soundSprite);

        gameHUD.attachChild(menuSprite);

        gameHUD.registerTouchArea(placeTilesYes);
        gameHUD.registerTouchArea(placeTilesNo);
        gameHUD.registerTouchArea(soundSprite);

        gameHUD.registerTouchArea(menuSprite);
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return world.onSceneTouchEvent(pScene, pSceneTouchEvent);
    }

    private void setTilePlacementMode(int modePlaceTiles) {
        if (modePlaceTiles == World.MODE_PLACE_TILES) {
            placeTilesYes.setCurrentTileIndex(1);
            placeTilesNo.setCurrentTileIndex(0);
        } else if (modePlaceTiles == World.MODE_DELETE_TILES) {
            placeTilesYes.setCurrentTileIndex(0);
            placeTilesNo.setCurrentTileIndex(1);
        }
    }

    public static void setGameMode(int pmode) {
        mode = pmode;
    }
}
