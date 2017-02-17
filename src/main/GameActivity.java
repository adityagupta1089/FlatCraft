package main;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import manager.ResourcesManager;
import manager.SceneManager;

public class GameActivity extends BaseGameActivity {

	private Camera camera;

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, ResourcesManager.WIDTH, ResourcesManager.HEIGHT);
		EngineOptions engineoptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(ResourcesManager.WIDTH, ResourcesManager.HEIGHT), this.camera);
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
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.createMenuScene();
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

}
