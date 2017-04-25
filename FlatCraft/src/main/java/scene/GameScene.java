package scene;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import base.BaseScene;
import csp203.flatcraft.R;
import hud.FlatCraftHUD;
import manager.ResourcesManager;
import manager.SceneManager;
import scene.constants.GameModes;
import scene.constants.VolumePreferences;
import world.World;
import world.multi.MultiPlayerWorld;
import world.single.CreativeWorld;
import world.single.SurvivalWorld;

public class GameScene extends BaseScene implements IOnSceneTouchListener, GameModes, VolumePreferences {
    private static int mode;
    // --------------------------------------------------------------//
    // Variables
    // --------------------------------------------------------------//
    private FlatCraftHUD gameHUD;
    private World world;
    private PhysicsWorld physicsWorld;
    private TiledSprite placeTilesYes;
    private TiledSprite placeTilesNo;
    private List<IEntity> entities;


    public static void setGameMode(int pmode) {
        mode = pmode;
    }

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

    }

    @Override
    public void onBackKeyPressed() {
        ResourcesManager.buttonClickSound.play();
        if (gameHUD.inventorySceneShown) {
            gameHUD.resetInventory();
            return;
        }
        if (mode == MODE_MULTI_PLAYER) {
            ((MultiPlayerWorld) world).exit();
        }
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
        if (mode == MODE_SINGLE_CREATIVE) world = new CreativeWorld(camera);
        else if (mode == MODE_MULTI_PLAYER) world = new MultiPlayerWorld(camera);
        else if (mode == MODE_SINGLE_SURVIVAL) world = new SurvivalWorld(camera);
        ResourcesManager.world = world;
        attachChild(world);
    }

    private void createPhysics() {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0f, -17f), false);
        registerUpdateHandler(physicsWorld);
    }

    private void createAnalogOnScreenController() {
        final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(200, 120, camera, ResourcesManager
                .mOnScreenControlBaseTextureRegion, ResourcesManager.mOnScreenControlKnobTextureRegion, 0.1f, 200,
                vertexBufferObjectManager, new IAnalogOnScreenControlListener() {
            @Override
            public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float
                    pValueY) {
                if (mode != MODE_MULTI_PLAYER) {
                    world.player.setVelocityDirection(pValueX, pValueY);
                } else {
                    ((MultiPlayerWorld) world).setPlayerVelocityDirection(pValueX, pValueY);
                }
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

        placeTilesYes = new TiledSprite(1700, 180, ResourcesManager.placeTilesYesRegion, vertexBufferObjectManager) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
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

        placeTilesNo = new TiledSprite(1700, 380, ResourcesManager.placeTilesNoRegion, vertexBufferObjectManager) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    ResourcesManager.buttonClickSound.play();
                    world.setPlaceMode(World.MODE_DELETE_TILES);
                    setTilePlacementMode(World.MODE_DELETE_TILES);
                    return true;
                }
                return false;
            }
        };

        Sprite menuSprite = new Sprite(1700, 900, ResourcesManager.menuRegion, vertexBufferObjectManager) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    ResourcesManager.buttonClickSound.play();
                    createMenuScene();
                    return true;
                }
                return false;
            }
        };

        Sprite moreTiles = new Sprite(1525, 180, ResourcesManager.moreTilesRegion, vertexBufferObjectManager) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                if (pSceneTouchEvent.isActionUp()) {
                    ResourcesManager.buttonClickSound.play();
                    gameHUD.inventorySceneToggle();
                    return true;
                }

                return false;
            }
        };

        placeTilesYes.setScale(1.5f);
        placeTilesNo.setScale(1.5f);
        menuSprite.setScale(1.5f * menuSprite.getHeight() / placeTilesYes.getHeight());
        moreTiles.setScale(1.5f * moreTiles.getHeight() / placeTilesYes.getHeight());

        gameHUD.attachChild(placeTilesYes);
        gameHUD.attachChild(placeTilesNo);
        gameHUD.attachChild(moreTiles);
        gameHUD.attachChild(menuSprite);

        gameHUD.registerTouchArea(placeTilesYes);
        gameHUD.registerTouchArea(placeTilesNo);
        gameHUD.registerTouchArea(moreTiles);
        gameHUD.registerTouchArea(menuSprite);
    }

    private void createMenuScene() {
        ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Context context = ResourcesManager.gameActivity;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.options, null);
                builder.setView(dialogView);
                builder.setTitle("Options");
                final SeekBar mfxSeekBar = (SeekBar) dialogView.findViewById(R.id.seekBar1);
                mfxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        engine.getMusicManager().setMasterVolume(progress / 100f);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //nothing to do
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        ResourcesManager.mfxVol = seekBar.getProgress() / 100f;
                    }
                });
                final SeekBar sfxSeekBar = (SeekBar) dialogView.findViewById(R.id.seekBar2);
                sfxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        engine.getSoundManager().setMasterVolume(progress / 100f);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        //nothing to do
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        ResourcesManager.sfxVol = seekBar.getProgress() / 100f;
                    }
                });
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ResourcesManager.buttonClickSound.play();
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                onBackKeyPressed();
                                break;
                            case DialogInterface.BUTTON_NEUTRAL:
                                break;
                        }
                    }
                };
                builder.setPositiveButton("Resume", dialogClickListener).setNegativeButton("Exit",
                        dialogClickListener);
                builder.setCancelable(true);
                builder.show();
                mfxSeekBar.setProgress((int) (ResourcesManager.engine.getMusicManager().getMasterVolume() * 100));
                sfxSeekBar.setProgress((int) (ResourcesManager.engine.getSoundManager().getMasterVolume() * 100));
            }
        });
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        return world.onSceneTouchEvent(pScene, pSceneTouchEvent);
    }

    public void setTilePlacementMode(int modePlaceTiles) {
        if (modePlaceTiles == World.MODE_PLACE_TILES) {
            placeTilesYes.setCurrentTileIndex(1);
            placeTilesNo.setCurrentTileIndex(0);
        } else if (modePlaceTiles == World.MODE_DELETE_TILES) {
            placeTilesYes.setCurrentTileIndex(0);
            placeTilesNo.setCurrentTileIndex(1);
        }
    }

    public World getWorld() {
        return world;
    }


}
