package main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import manager.ResourcesManager;
import manager.SceneManager;
import scene.VolumePreferences;

public class GameActivity extends BaseGameActivity implements VolumePreferences {

    private BoundCamera camera;

    @Override
    public EngineOptions onCreateEngineOptions() {
        camera = new BoundCamera(0, 0, ResourcesManager.WIDTH, ResourcesManager.HEIGHT);
        EngineOptions engineoptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new RatioResolutionPolicy(ResourcesManager.WIDTH, ResourcesManager.HEIGHT),
                this.camera);
        engineoptions.getAudioOptions().setNeedsMusic(true);
        engineoptions.getAudioOptions().setNeedsSound(true);
        engineoptions.getRenderOptions().setDithering(true);
        return engineoptions;
    }

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions) {
        return new LimitedFPSEngine(pEngineOptions, 60);
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) {
        ResourcesManager.prepare(this, camera, getVertexBufferObjectManager(), mEngine);
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
        SceneManager.createSplashScene(pOnCreateSceneCallback);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
        SharedPreferences sharedPreferences
                = getSharedPreferences(VOLUME_PREFERENCES, Context.MODE_PRIVATE);
        ResourcesManager.mfxVol = sharedPreferences.getInt(MFX_VOL, 100) / 100f;
        ResourcesManager.sfxVol = sharedPreferences.getInt(SFX_VOL, 100) / 100f;
        mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
            public void onTimePassed(final TimerHandler pTimerHandler) {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.createMenuScene();
            }
        }));
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SceneManager.getCurrentScene().onBackKeyPressed();
        }
        return false;
    }

    @Override
    protected void onPause() {
        mEngine.getMusicManager().setMasterVolume(0);
        mEngine.getSoundManager().setMasterVolume(0);
        super.onPause();
    }

    @Override
    protected synchronized void onResume() {
        mEngine.getMusicManager().setMasterVolume(ResourcesManager.mfxVol);
        mEngine.getSoundManager().setMasterVolume(ResourcesManager.sfxVol);
        super.onResume();
    }
}
